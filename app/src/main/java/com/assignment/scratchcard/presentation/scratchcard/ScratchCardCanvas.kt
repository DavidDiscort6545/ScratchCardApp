package com.assignment.scratchcard.presentation.scratchcard

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get

@Composable
fun ScratchCardCanvas(
    lines: List<List<Offset>>,
    cardCodeDisplayText: String,
    onScratchStateChanged: (List<Offset> , Float) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
) {

    // helper path for current movement
    var currentPathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // 1. helper bitmap for calculations (smaller resolution for speed and resource saving)
    val calcWidth = 100
    val calcHeight = 100
    val helperBitmap = remember {
        createBitmap(calcWidth, calcHeight, Bitmap.Config.ALPHA_8)
    }
    val helperCanvas = remember { Canvas(helperBitmap) }
    val helperPaint = remember {
        Paint().apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 20f //This value needs to match with the small bitmap, change with care (original value 100f)
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            // Using SRC_OVER, because in ALPHA_8 bitmap we draw just swoops
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER)
            color = android.graphics.Color.BLACK // full intensity
            alpha = 255
        }
    }


    val editableModifier = if (isEditable) {
        Modifier.pointerInput(lines) {
            detectDragGestures(
                onDragStart = { offset -> currentPathPoints = listOf(offset) },
                onDragEnd = {

                    // 1. Get current view width + height (display size)
                    val viewWidth = size.width.toFloat()
                    val viewHeight = size.height.toFloat()

                    // 2. Join current strokes with those who was send from past
                    val currentStroke = currentPathPoints
                    val allLinesForCalculation = lines + listOf(currentStroke)

                    if (viewWidth > 0 && viewHeight > 0) {
                        // Calculate coefficient (like 100 / 1080 = 0.09)
                        val scaleX = calcWidth.toFloat() / viewWidth
                        val scaleY = calcHeight.toFloat() / viewHeight

                        // 3. Clear helper bitmap - used later calculation of scratched %
                        helperBitmap.eraseColor(android.graphics.Color.TRANSPARENT)

                        // 4. Draw all lines to small helper canvas
                        allLinesForCalculation.forEach { line ->
                            drawLineInHelperCanvas(line, scaleX, scaleY, helperCanvas, helperPaint)
                        }

                        // 5. Count pixels, which are not transparent (not 0)
                        val scratchedCountFloat = scratchedPixelsCount(calcWidth, calcHeight, helperBitmap).toFloat()

                        val finalProgress = scratchedCountFloat / (calcWidth * calcHeight)
                        onScratchStateChanged(currentStroke, finalProgress)
                    }
                    currentPathPoints = emptyList()
                }
            ) { change, _ ->
                change.consume()
                currentPathPoints = currentPathPoints + change.position // here we are accumulation change to the path
            }
        }
    } else Modifier


    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // 1. bottom layer (Card content - Text with code)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(cardCodeDisplayText, style = MaterialTheme.typography.headlineLarge)
        }

        // 2. top layer (scratch surface)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(editableModifier)
        ) {
            // we will draw light gray area, which will we erase by finger movement
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // the scratching are - could be replace with different resource file in future version
                drawRect(Color.LightGray)

                // draw all saved paths
                lines.forEach { line ->
                    drawScratchLine(line)
                }

                // here we are drawing currently drawn line
                if (currentPathPoints.isNotEmpty()) {
                    drawScratchLine(currentPathPoints)
                }

                restoreToCount(checkPoint)
            }
        }
    }
}

private fun scratchedPixelsCount(
    calcWidth: Int,
    calcHeight: Int,
    helperBitmap: Bitmap
): Int {
    var scratchedCount = 0
    for (x in 0 until calcWidth) {
        for (y in 0 until calcHeight) {
            // getPixel in ALPHA_8 returns values from 0 (transparent) to 255
            if (helperBitmap[x, y] != 0) {
                scratchedCount++
            }
        }
    }
    return scratchedCount
}

private fun drawLineInHelperCanvas(
    line: List<Offset>,
    scaleX: Float,
    scaleY: Float,
    helperCanvas: Canvas,
    helperPaint: Paint
) {
    if (line.size > 1) {
        val p = android.graphics.Path()
        //Set first point for line - scaled to 100x100
        p.moveTo(line.first().x * scaleX, line.first().y * scaleY)

        line.forEach { point ->
            p.lineTo(point.x * scaleX, point.y * scaleY)
        }
        helperCanvas.drawPath(p, helperPaint)
    }
}

// helper function for drawing of 1 line for erasing upper layer
private fun DrawScope.drawScratchLine(points: List<Offset>) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.forEach { lineTo(it.x, it.y) }
    }
    drawPath(
        path = path,
        color = Color.Transparent,
        style = Stroke(width = 100f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        blendMode = BlendMode.Clear
    )
}