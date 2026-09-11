package ir.docscan.app.ui.screens.crop

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.docscan.app.R
import ir.docscan.app.ui.theme.BackgroundDark
import ir.docscan.app.ui.theme.PrimaryTeal
import ir.docscan.app.ui.theme.SurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropAdjustScreen(
    docId: String,
    onNavigateBack: () -> Unit,
    onProceedToPreview: (String) -> Unit
) {
    var rotationAngle by remember { mutableStateOf(0f) }

    // Initial mock corner coordinates for interactive handle dragging
    var corners by remember {
        mutableStateOf(
            CropCorners(
                topLeft = Offset(100f, 160f),
                topRight = Offset(620f, 140f),
                bottomRight = Offset(640f, 920f),
                bottomLeft = Offset(80f, 900f)
            )
        )
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
                            // Reset crop coordinates
                            corners = CropCorners(
                                topLeft = Offset(80f, 140f),
                                topRight = Offset(640f, 140f),
                                bottomRight = Offset(640f, 920f),
                                bottomLeft = Offset(80f, 920f)
                            )
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
                                // Simulate auto edge detection snap
                                corners = CropCorners(
                                    topLeft = Offset(95f, 155f),
                                    topRight = Offset(625f, 145f),
                                    bottomRight = Offset(635f, 905f),
                                    bottomLeft = Offset(85f, 895f)
                                )
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
                            onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
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
                        onClick = { onProceedToPreview(docId) },
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
            // Document Placeholder / View Canvas simulating photographed paper
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
