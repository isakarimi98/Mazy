package ir.docscan.app.ui.screens.preview

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.docscan.app.R
import ir.docscan.app.data.mock.SampleDocs
import ir.docscan.app.data.model.DocFilterType
import ir.docscan.app.ui.screens.export.ExportPdfSheet
import ir.docscan.app.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPreviewScreen(
    docId: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val currentDoc = remember(docId) {
        SampleDocs.initialDocuments.find { it.id == docId } ?: SampleDocs.initialDocuments.first()
    }

    var activeFilter by remember { mutableStateOf(currentDoc.activeFilter) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentDoc.title,
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
                    FilledTonalButton(
                        onClick = { showExportSheet = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            FilterSelectorBar(
                selectedFilter = activeFilter,
                onFilterSelected = { activeFilter = it }
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
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.88f)
                    .shadow(12.dp, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (activeFilter) {
                        DocFilterType.PHOTOCOPY -> Color(0xFFFAFAFA)
                        DocFilterType.BW_OFFICE -> Color(0xFFFFFFFF)
                        DocFilterType.WHITEBOARD -> Color(0xFFFCFDFF)
                        DocFilterType.MAGIC_COLOR -> Color(0xFFFBFBFB)
                        DocFilterType.ORIGINAL -> Color(0xFFF3EFE0) // Warm paper hue
                    }
                )
            ) {
                // Interior representation of the document
                Crossfade(targetState = activeFilter, label = "filter_crossfade") { filter ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Header of document
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (filter == DocFilterType.MAGIC_COLOR) PrimaryTeal.copy(alpha = 0.2f)
                                            else Color.DarkGray.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = if (filter == DocFilterType.MAGIC_COLOR) PrimaryTeal else Color.DarkGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "شماره: ۱۲۸۴-د",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (filter == DocFilterType.BW_OFFICE) Color.Black else Color.DarkGray
                                    )
                                    Text(
                                        text = "تاریخ: ۱۴۰۳/۰۶/۱۵",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (filter == DocFilterType.BW_OFFICE) Color.Black else Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Simulated text lines with contrast corresponding to filter
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val lineAlpha = when (filter) {
                                    DocFilterType.PHOTOCOPY -> 0.95f
                                    DocFilterType.BW_OFFICE -> 1.0f
                                    DocFilterType.WHITEBOARD -> 0.85f
                                    DocFilterType.MAGIC_COLOR -> 0.90f
                                    DocFilterType.ORIGINAL -> 0.65f
                                }

                                val lineColor = if (filter == DocFilterType.BW_OFFICE) Color.Black else Color(0xFF1E293B)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(10.dp)
                                        .background(lineColor.copy(alpha = lineAlpha), RoundedCornerShape(2.dp))
                                )

                                repeat(6) { index ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(if (index % 2 == 0) 1f else 0.88f)
                                            .height(8.dp)
                                            .background(lineColor.copy(alpha = lineAlpha), RoundedCornerShape(2.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Stamp / Signature Simulation Box
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .border(
                                                width = 2.dp,
                                                color = if (filter == DocFilterType.MAGIC_COLOR) Color(0xFF0F766E)
                                                else if (filter == DocFilterType.BW_OFFICE) Color.Black
                                                else Color(0xFF334155),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "ممهور شد",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (filter == DocFilterType.MAGIC_COLOR) Color(0xFF0F766E)
                                            else if (filter == DocFilterType.BW_OFFICE) Color.Black
                                            else Color(0xFF334155)
                                        )
                                    }
                                }
                            }

                            // Footer tag
                            Text(
                                text = "اسکن‌شده توسط DocScan (نسخه کاتلین)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExportSheet) {
        ExportPdfSheet(
            docTitle = currentDoc.title,
            onDismiss = { showExportSheet = false },
            onDirectPrint = {
                // Direct print action via Android PrintManager
            },
            onShare = {
                // FileProvider intent share
            },
            onSavePdf = {
                onNavigateHome()
            }
        )
    }
}
