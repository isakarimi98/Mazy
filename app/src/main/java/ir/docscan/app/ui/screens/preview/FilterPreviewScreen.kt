package ir.docscan.app.ui.screens.preview

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.docscan.app.R
import ir.docscan.app.data.model.DocFilterType
import ir.docscan.app.data.pdf.PdfGenerator
import ir.docscan.app.data.processor.ScanSessionManager
import ir.docscan.app.data.util.ShareHelper
import ir.docscan.app.ui.screens.export.ExportPdfSheet
import ir.docscan.app.ui.theme.PrimaryTeal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPreviewScreen(
    docId: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val processedBitmap by ScanSessionManager.processedBitmap.collectAsState()

    var activeFilter by remember { mutableStateOf(ScanSessionManager.activeFilter) }
    var showExportSheet by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val title = ScanSessionManager.docTitle

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = stringResource(R.string.title_preview),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                actions = {
                    // Export PDF Button
                    FilledTonalButton(
                        onClick = { showExportSheet = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_export_pdf),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Save Document Button
                    IconButton(
                        onClick = {
                            if (!isSaving) {
                                isSaving = true
                                coroutineScope.launch {
                                    ScanSessionManager.saveCurrentDoc(context)
                                    isSaving = false
                                    onNavigateHome()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "ذخیره در اسناد",
                            tint = PrimaryTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            FilterSelectorBar(
                selectedFilter = activeFilter,
                onFilterSelected = { filter ->
                    activeFilter = filter
                    coroutineScope.launch {
                        ScanSessionManager.changeFilter(filter)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // Document Simulated Paper Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .fillMaxHeight(0.90f)
                    .shadow(12.dp, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val bmp = processedBitmap
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "سند پردازش شده",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                }
            }
        }
    }

    if (showExportSheet) {
        ExportPdfSheet(
            docTitle = title,
            onDismiss = { showExportSheet = false },
            onDirectPrint = {
                processedBitmap?.let { bmp ->
                    ShareHelper.printBitmap(context, bmp, title)
                }
            },
            onShare = {
                processedBitmap?.let { bmp ->
                    coroutineScope.launch {
                        val pdfFile = PdfGenerator.generatePdf(context, title, listOf(bmp))
                        ShareHelper.sharePdf(context, pdfFile, title)
                    }
                }
            },
            onSavePdf = {
                processedBitmap?.let { bmp ->
                    coroutineScope.launch {
                        ScanSessionManager.saveCurrentDoc(context)
                        val pdfFile = PdfGenerator.generatePdf(context, title, listOf(bmp))
                        snackbarHostState.showSnackbar("فایل PDF در حافظه برنامه ذخیره شد")
                        onNavigateHome()
                    }
                }
            }
        )
    }
}

@Composable
fun FilterSelectorBar(
    selectedFilter: DocFilterType,
    onFilterSelected: (DocFilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.filter_photocopy),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DocFilterType.values().forEach { filter ->
                    val isSelected = filter == selectedFilter

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onFilterSelected(filter) }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (filter) {
                                    DocFilterType.PHOTOCOPY -> Icons.Default.FilterFrames
                                    DocFilterType.BW_OFFICE -> Icons.Default.Contrast
                                    DocFilterType.WHITEBOARD -> Icons.Default.BrightnessAuto
                                    DocFilterType.MAGIC_COLOR -> Icons.Default.AutoAwesome
                                    DocFilterType.ORIGINAL -> Icons.Default.Image
                                },
                                contentDescription = filter.titleFa,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = filter.titleFa,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
