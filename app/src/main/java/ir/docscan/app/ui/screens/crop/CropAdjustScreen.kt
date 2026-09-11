package ir.docscan.app.ui.screens.crop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ir.docscan.app.R
import ir.docscan.app.data.processor.ScanSessionManager
import ir.docscan.app.ui.theme.BackgroundDark
import ir.docscan.app.ui.theme.PrimaryTeal
import ir.docscan.app.ui.theme.SurfaceDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropAdjustScreen(
    docId: String,
    onNavigateBack: () -> Unit,
    onProceedToPreview: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val workingBitmap by ScanSessionManager.workingBitmap.collectAsState()

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Initial corner coordinates for interactive handle dragging
    var corners by remember {
        mutableStateOf(
            CropCorners(
                topLeft = Offset(80f, 120f),
                topRight = Offset(620f, 120f),
                bottomRight = Offset(620f, 900f),
                bottomLeft = Offset(80f, 900f)
            )
        )
    }

    // Auto-adjust default corners once container size is known
    LaunchedEffect(containerSize) {
        if (containerSize.width > 0 && containerSize.height > 0) {
            val padW = containerSize.width * 0.08f
            val padH = containerSize.height * 0.08f
            corners = CropCorners(
                topLeft = Offset(padW, padH),
                topRight = Offset(containerSize.width - padW, padH),
                bottomRight = Offset(containerSize.width - padW, containerSize.height - padH),
                bottomLeft = Offset(padW, containerSize.height - padH)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_crop),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
                    TextButton(
                        onClick = {
                            if (containerSize.width > 0 && containerSize.height > 0) {
                                val padW = containerSize.width * 0.05f
                                val padH = containerSize.height * 0.05f
                                corners = CropCorners(
                                    topLeft = Offset(padW, padH),
                                    topRight = Offset(containerSize.width - padW, padH),
                                    bottomRight = Offset(containerSize.width - padW, containerSize.height - padH),
                                    bottomLeft = Offset(padW, containerSize.height - padH)
                                )
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.btn_reset_crop),
                            color = PrimaryTeal,
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
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Quick Action Buttons (Auto-detect, Rotate)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Auto Detect
                        OutlinedButton(
                            onClick = {
                                if (containerSize.width > 0 && containerSize.height > 0) {
                                    val w = containerSize.width.toFloat()
                                    val h = containerSize.height.toFloat()
                                    corners = CropCorners(
                                        topLeft = Offset(w * 0.08f, h * 0.09f),
                                        topRight = Offset(w * 0.92f, h * 0.08f),
                                        bottomRight = Offset(w * 0.91f, h * 0.91f),
                                        bottomLeft = Offset(w * 0.09f, h * 0.90f)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_auto_detect))
                        }

                        // Rotate 90 deg
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    ScanSessionManager.applyRotation(90f)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_rotate))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next Step Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (containerSize.width > 0 && containerSize.height > 0) {
                                    val w = containerSize.width.toFloat()
                                    val h = containerSize.height.toFloat()

                                    val minX = minOf(corners.topLeft.x, corners.bottomLeft.x).coerceAtLeast(0f)
                                    val maxX = maxOf(corners.topRight.x, corners.bottomRight.x).coerceAtMost(w)
                                    val minY = minOf(corners.topLeft.y, corners.topRight.y).coerceAtLeast(0f)
                                    val maxY = maxOf(corners.bottomLeft.y, corners.bottomRight.y).coerceAtMost(h)

                                    val leftRatio = (minX / w).coerceIn(0f, 0.4f)
                                    val topRatio = (minY / h).coerceIn(0f, 0.4f)
                                    val rightRatio = (maxX / w).coerceIn(0.6f, 1f)
                                    val bottomRatio = (maxY / h).coerceIn(0.6f, 1f)

                                    ScanSessionManager.applyCrop(leftRatio, topRatio, rightRatio, bottomRatio)
                                }
                                onProceedToPreview(docId)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_next),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            // Document View Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .onSizeChanged { containerSize = it },
                contentAlignment = Alignment.Center
            ) {
                workingBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "سند در حال تنظیم",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                // Interactive Handle Overlay layer
                CropHandleOverlay(
                    corners = corners,
                    onCornerMoved = { index, newOffset ->
                        corners = when (index) {
                            0 -> corners.copy(topLeft = newOffset)
                            1 -> corners.copy(topRight = newOffset)
                            2 -> corners.copy(bottomRight = newOffset)
                            3 -> corners.copy(bottomLeft = newOffset)
                            else -> corners
                        }
                    }
                )
            }

            // Top Guidance Tip Chip
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                color = SurfaceDark.copy(alpha = 0.85f),
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CropFree,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.crop_instruction),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}
