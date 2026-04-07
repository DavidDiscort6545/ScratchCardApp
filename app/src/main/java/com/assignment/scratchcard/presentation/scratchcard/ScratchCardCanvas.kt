package com.assignment.scratchcard.presentation.scratchcard

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun ScratchCardCanvas(
    lines: List<List<Offset>>, // Stav prichádza z ViewModelu
//    onNewPathCreated: (List<Offset>) -> Unit, // Spätná väzba do ViewModelu
//    onProgressChanged: (Float) -> Unit,
    onScratchStateChanged: (List<Offset> , Float) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    // we will save finger movement into this state
    val pathState = remember { mutableStateOf(Path()) } //TODO remove if not needed

    // Canvas needs to know how path has changed
    val path = pathState.value //TODO remove if not needed

    // helper path for current movement
    var currentPathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // 1. Pomocná bitmapa na výpočet (menšie rozlíšenie pre rýchlosť)
    val calcWidth = 100
    val calcHeight = 100
    val helperBitmap = remember {
        createBitmap(calcWidth, calcHeight, Bitmap.Config.ALPHA_8) //ARGB_8888
    }
    val helperCanvas = remember { android.graphics.Canvas(helperBitmap) }
    val helperPaint = remember {
        Paint().apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 20f // Musí byť v pomere k malej bitmape (pôvodne 100f na veľkej)
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
            // Použijeme SRC_OVER, lebo v ALPHA_8 bitmape len kreslíme "stopy"
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
                    println("História čiar: ${lines.size}, Aktuálna: ${currentPathPoints.size}")

                    // Výpočet robíme s pevnou kópiou oboch
                    //val allPoints = lines + listOf(currentPathPoints)


                    // 1. Získame aktuálne rozmery Canvasu (veľkosť na displeji)
                    val viewWidth = size.width.toFloat()
                    val viewHeight = size.height.toFloat()

                    // 1. Zoberieme si BODY, ktoré máme TERAZ v parametri 'lines'
                    // a PRIPOČÍTAME k nim tie, ktoré sme práve doťahali.
                    val currentStroke = currentPathPoints
                    val allLinesForCalculation = lines + listOf(currentStroke)

                    // 2. Odošleme do ViewModelu (toto prebehne na pozadí)
                    //onNewPathCreated(currentStroke)

                    if (viewWidth > 0 && viewHeight > 0) {
                        // 2. Vypočítame koeficient (napr. 100 / 1080 = 0.09)
                        val scaleX = calcWidth.toFloat() / viewWidth
                        val scaleY = calcHeight.toFloat() / viewHeight

                        // Vyčistíme bitmapu (nastavíme ju na "nulu")
                        helperBitmap.eraseColor(android.graphics.Color.TRANSPARENT)

                        // 3. Vykreslíme všetky čiary do malej bitmapy
                        //val allLines = lines + listOf(currentPathPoints)
                        allLinesForCalculation.forEach { line ->
                            if (line.size > 1) {
                                val p = android.graphics.Path()
                                // Prvý bod čiary preškálovaný do 100x100
                                p.moveTo(line.first().x * scaleX, line.first().y * scaleY)

                                line.forEach { point ->
                                    p.lineTo(point.x * scaleX, point.y * scaleY)
                                }
                                helperCanvas.drawPath(p, helperPaint)
                            }
                        }

                        // 4. Spočítaš pixely, ktoré sú "zafarbené" (nie sú 0)
                        var scratchedCount = 0
                        for (x in 0 until calcWidth) {
                            for (y in 0 until calcHeight) {
                                // getPixel v ALPHA_8 vráti hodnotu od 0 (priehľadná) po 255
                                if (helperBitmap.getPixel(x, y) != 0) {
                                    scratchedCount++
                                }
                            }
                        }

                        val finalProgress = scratchedCount.toFloat() / (calcWidth * calcHeight)
                        onScratchStateChanged(currentStroke, finalProgress)
                    }

                    println("História čiar: ${lines.size}, Aktuálna: ${currentPathPoints.size}")


//                    onNewPathCreated(currentPathPoints)
                    currentPathPoints = emptyList()

//                    //----
//                    // 1. Najprv ulož aktuálnu čiaru do ViewModelu
//                    val finalPoints = currentPathPoints
//                    onNewPathCreated(finalPoints)
//
//                    // 2. Výpočet rob s kompletným zoznamom (história + aktuálna)
//                    val allLinesToCalculate = lines + listOf(finalPoints)
//
//                    // ... tu ide ten istý kód s helperBitmap a helperCanvas ...
//                    // ... ale použi allLinesToCalculate v tom forEach cykle ...
//
//                    allLinesToCalculate.forEach { line ->
//                        // kreslenie do helperCanvas...
//                    }
//
//                    // 3. Resetni lokálny bod
//                    currentPathPoints = emptyList()





//
//                    // Vykreslíme aktuálny stav do pomocnej bitmapy
//                    helperBitmap.eraseColor(android.graphics.Color.TRANSPARENT)
//
//                    // Musíme škálovať súradnice z reálneho Canvasu do 100x100
//                    val scaleX = calcWidth.toFloat() / size.width
//                    val scaleY = calcHeight.toFloat() / size.height
//
//                    (lines + listOf(currentPathPoints)).forEach { line ->
//                        if (line.size > 1) {
//                            val p = android.graphics.Path()
//                            p.moveTo(line[0].x * scaleX, line[0].y * scaleY)
//                            line.forEach { point ->
//                                p.lineTo(point.x * scaleX, point.y * scaleY)
//                            }
//                            helperCanvas.drawPath(p, helperPaint)
//                        }
//                    }
//
//                    // spočítame netransparentné pixely (tie, čo sme "poškrabali")
//                    var scratchedPixels = 0
//                    for (x in 0 until calcWidth) {
//                        for (y in 0 until calcHeight) {
//                            if (helperBitmap.getPixel(x, y) != 0) scratchedPixels++
//                        }
//                    }

                    //val progress = scratchedPixels.toFloat() / (calcWidth * calcHeight)
                    //onProgressChanged(progress)
                }
            ) { change, _ ->
                change.consume()
                currentPathPoints = currentPathPoints + change.position
            }
        }
    } else Modifier


    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // 1. bottom layer (Card content like Text with code)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Code: XXX-XXX", style = MaterialTheme.typography.headlineLarge)
        }

        // 2. top layer (scratch surface)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(editableModifier)
//                .pointerInput(Unit) {
//                    detectDragGestures(
//                        onDragStart = { offset ->
//                            path.moveTo(offset.x, offset.y)//for correct path initiation
//                        },
//                        onDragEnd = {
//                            onNewPathCreated(currentPathPoints)
//                            currentPathPoints = emptyList()
//                            // TU zavoláš výpočet percent z bitmapy po zdvihnutí prsta
//                            onProgressChanged(0.85f) // Len príklad
//                        }
//                    ) { change, dragAmount ->
//                        change.consume()
//                        // we will add points into path while finger is moving
//                        path.lineTo(change.position.x, change.position.y)
//
//                        //TODO check if this could be done less frequently for optimization
//
//                        // by this we will tell Compose, to redraw Canvas
//                        pathState.value = Path().apply { addPath(path) }
//                    }
//                }
        ) {
            // we will draw light gray area, which will we erase by finger movement
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // the scratching are - could be replace with different resource file in future version
                drawRect(Color.LightGray)

//                // "Rubber" - we are drawing path with BlendMode.Clear
//                drawPath(
//                    path = path,
//                    color = Color.Transparent,
//                    style = Stroke(
//                        width = 80f,
//                        cap = StrokeCap.Round,
//                        join = StrokeJoin.Round
//                    ),
//                    blendMode = BlendMode.Clear
//                )

                // Vykreslíme všetky uložené čiary
                lines.forEach { line ->
                    drawScratchLine(line)
                }

                // Vykreslíme aktuálne prebiehajúcu čiaru
                if (currentPathPoints.isNotEmpty()) {
                    drawScratchLine(currentPathPoints)
                }

                restoreToCount(checkPoint)
            }
        }
    }
}

// Pomocná funkcia na vykreslenie jednej čiary ako "gumy"
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