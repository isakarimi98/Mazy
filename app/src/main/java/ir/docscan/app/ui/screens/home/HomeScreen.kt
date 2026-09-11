package ir.docscan.app.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ir.docscan.app.R
import ir.docscan.app.data.util.ShareHelper
import ir.docscan.app.ui.screens.home.components.DocItemCard
import ir.docscan.app.ui.theme.PrimaryTeal
import ir.docscan.app.ui.theme.StatusGreen
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCrop: (String) -> Unit,
    onNavigateToPreview: (String) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var renameInputText by remember { mutableStateOf("") }

    // Launcher: Camera Take Picture
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            viewModel.onMediaSelected(tempCameraUri!!, isCamera = true) {
                onNavigateToCrop("doc-new")
            }
        }
    }

    // Launcher: Camera Permission Request
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera(context) { uri ->
                tempCameraUri = uri
                takePictureLauncher.launch(uri)
            }
        }
    }

    // Launcher: Pick Visual Media (Gallery)
    val pickGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onMediaSelected(uri, isCamera = false) {
                onNavigateToCrop("doc-new")
            }
        }
    }

    val filteredDocs = remember(state.documents, state.searchQuery) {
        if (state.searchQuery.isBlank()) {
            state.documents
        } else {
            state.documents.filter {
                it.title.contains(state.searchQuery, ignoreCase = true) ||
                it.dateShamsi.contains(state.searchQuery)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.isSearchActive) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("جستجو در اسناد و مدارک...") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Column {
                            Text(
                                text = stringResource(R.string.title_home),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = StatusGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "۱۰۰٪ آفلاین و بدون دسترسی اینترنت",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusGreen
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSearch(!state.isSearchActive) }) {
                        Icon(
                            imageVector = if (state.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "جستجو"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showNewScanOptions(true) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "اسکن سند جدید",
                        tint = Color.White
                    )
                },
                text = {
                    Text(
                        text = "اسکن سند جدید",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = PrimaryTeal,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (filteredDocs.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.empty_docs_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.empty_docs_desc),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDocs, key = { it.id }) { doc ->
                        DocItemCard(
                            doc = doc,
                            onClick = {
                                viewModel.openExistingDoc(doc) {
                                    onNavigateToPreview(doc.id)
                                }
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(doc.id) },
                            onRename = {
                                renameInputText = doc.title
                                viewModel.setDocToRename(doc)
                            },
                            onDelete = {
                                viewModel.setDocToDelete(doc)
                            },
                            onShare = {
                                val imgFile = doc.imagePath?.let { File(it) }
                                if (imgFile != null && imgFile.exists()) {
                                    ShareHelper.shareImage(context, imgFile, doc.title)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for "New Document" (Camera vs Gallery)
    if (state.showNewScanSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.showNewScanOptions(false) },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "افزودن سند یا مدرک جدید",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "روش بارگذاری سند را انتخاب نمایید:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Option 1: Camera Scan
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    onClick = {
                        viewModel.showNewScanOptions(false)
                        val hasCameraPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasCameraPermission) {
                            launchCamera(context) { uri ->
                                tempCameraUri = uri
                                takePictureLauncher.launch(uri)
                            }
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "عکس‌برداری فوری با دوربین",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "اسکن با بهینه‌سازی نور و تفکیک زوایا",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Option 2: Choose from Gallery
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    onClick = {
                        viewModel.showNewScanOptions(false)
                        pickGalleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoLibrary,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "انتخاب از گالری تصاویر",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "بارگذاری عکس ذخیره شده از حافظه داخلی",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Rename Dialog
    state.docToRename?.let { doc ->
        AlertDialog(
            onDismissRequest = { viewModel.setDocToRename(null) },
            title = { Text("تغییر نام سند", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameInputText,
                    onValueChange = { renameInputText = it },
                    label = { Text("عنوان جدید") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.renameDocument(doc.id, renameInputText) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setDocToRename(null) }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    state.docToDelete?.let { doc ->
        AlertDialog(
            onDismissRequest = { viewModel.setDocToDelete(null) },
            title = { Text("حذف سند", fontWeight = FontWeight.Bold) },
            text = { Text("آیا از حذف سند «${doc.title}» اطمینان دارید؟ این عمل غیرقابل بازگشت است.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteDocument(doc.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setDocToDelete(null) }) {
                    Text("انصراف")
                }
            }
        )
    }
}

private fun launchCamera(context: Context, onUriReady: (Uri) -> Unit) {
    try {
        val cacheDir = File(context.cacheDir, "camera").apply { mkdirs() }
        val imageFile = File(cacheDir, "scan_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        onUriReady(uri)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
