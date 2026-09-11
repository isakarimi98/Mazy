package ir.docscan.app.ui.screens.crop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import ir.docscan.app.ui.theme.PrimaryTeal

data class CropCorners(
    val topLeft: Offset,
    val topRight: Offset,
    val bottomRight: Offset,
    val bottomLeft: Offset
)

@Composable
fun CropHandleOverlay(
    corners: CropCorners,
    onCornerMoved: (cornerIndex: Int, newOffset: Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val handleRadius = 18.dp
    val handleColor = PrimaryTeal
    val strokeWidth = 3.dp

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(corners) {
                detectDragGestures { change, dragAmount ->
                    val touchPos = change.position
                    val threshold = 60f

                    val cornerList = listOf(
                        corners.topLeft,
                        corners.topRight,
                        corners.bottomRight,
                        corners.bottomLeft
                    )

                    // Find closest corner to touch
                    val closestIndex = cornerList.indexOfMinByOrNull { corner ->
                        (corner - touchPos).getDistance()
                    }

                    if (closestIndex != null && (cornerList[closestIndex] - touchPos).getDistance() < threshold) {
                        change.consume()
                        val updatedOffset = cornerList[closestIndex] + dragAmount
                        onCornerMoved(closestIndex, updatedOffset)
                    }
                }
            }
    ) {
        // Draw polygon path connecting 4 corners
        val path = Path().apply {
            moveTo(corners.topLeft.x, corners.topLeft.y)
            lineTo(corners.topRight.x, corners.topRight.y)
            lineTo(corners.bottomRight.x, corners.bottomRight.y)
            lineTo(corners.bottomLeft.x, corners.bottomLeft.y)
            close()
        }

        // 1. Draw outer shaded mask
        drawPath(
            path = path,
            color = Color.Transparent
        )

        // 2. Draw border lines connecting handles
        drawPath(
            path = path,
            color = handleColor,
            style = Stroke(width = strokeWidth.toPx())
        )

        // 3. Draw grid guideline lines (rule of thirds inside the crop area)
        for (i in 1..2) {
            val ratio = i / 3f
            // Horizontal guide line
            val leftPoint = lerp(corners.topLeft, corners.bottomLeft, ratio)
            val rightPoint = lerp(corners.topRight, corners.bottomRight, ratio)
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = leftPoint,
                end = rightPoint,
                strokeWidth = 1.5.dp.toPx()
            )

            // Vertical guide line
            val topPoint = lerp(corners.topLeft, corners.topRight, ratio)
            val bottomPoint = lerp(corners.bottomLeft, corners.bottomRight, ratio)
            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = topPoint,
                end = bottomPoint,
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // 4. Draw circular grab handles
        val cornerPoints = listOf(
            corners.topLeft,
            corners.topRight,
            corners.bottomRight,
            corners.bottomLeft
        )

        cornerPoints.forEach { point ->
            // Outer white ring
            drawCircle(
                color = Color.White,
                radius = handleRadius.toPx(),
                center = point
            )
            // Inner teal center
            drawCircle(
                color = handleColor,
                radius = (handleRadius - 4.dp).toPx(),
                center = point
            )
        }
    }
}

private fun lerp(start: Offset, stop: Offset, fraction: Float): Offset {
    return Offset(
        x = start.x + (stop.x - start.x) * fraction,
        y = start.y + (stop.y - start.y) * fraction
    )
}

private inline fun <T> List<T>.indexOfMinByOrNull(selector: (T) -> Float): Int? {
    if (isEmpty()) return null
    var minElem = get(0)
    var minValue = selector(minElem)
    var minIndex = 0
    for (i in 1 until size) {
        val value = selector(get(i))
        if (value < minValue) {
            minElem = get(i)
            minValue = value
            minIndex = i
        }
    }
    return minIndex
}
