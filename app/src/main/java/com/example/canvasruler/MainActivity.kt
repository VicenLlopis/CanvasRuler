package com.example.canvasruler

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.checkScrollableContainerConstraints
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()
        setContent {
            ContentView()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ContentView() {

    val lines = remember { mutableListOf<Pair<Offset, Offset>>() }

    val lastTouchx = remember {
        mutableStateOf(0f)
    }
    var drawLine by remember {
        mutableStateOf(false)
    }

    val lastTouchY = remember {
        mutableStateOf(0f)
    }

    val path = remember {
        mutableStateOf<Path?>(Path())
    }

    var positionXRect = 0f

    var positionYRect = 0f

    val sizeTheBox = remember {
        mutableStateOf(0f)
    }
    var widthBox = remember {
        mutableStateOf(200.dp)
    }

    var positionFinalLineX = 0f
    var positionFinalLineY = 0f
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInteropFilter { motionEvent ->
                        when (motionEvent.action) {

                            MotionEvent.ACTION_DOWN -> {
                                path.value?.moveTo(motionEvent.x, motionEvent.y)
                                //lines.add(Pair(Offset(motionEvent.x, motionEvent.y), Offset(motionEvent.x, motionEvent.y)))
                                lastTouchx.value = motionEvent.x
                                lastTouchY.value = motionEvent.y
                            }
                            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {

                                //val lastIndex = lines.lastIndex
                                // lines[lastIndex] = Pair(lines[lastIndex].first, Offset(motionEvent.x, motionEvent.y))

                                val historySize = motionEvent.historySize

                                for (i in 0 until historySize) {

                                    val historicalX = motionEvent.getHistoricalX(i)
                                    val historicalY = motionEvent.getHistoricalY(i)

                                    path.value?.lineTo(historicalX, historicalY)
                                }
                                path.value?.lineTo(motionEvent.x, motionEvent.y)

                                lastTouchx.value = motionEvent.x
                                lastTouchY.value = motionEvent.y
                            }
                        }
                        lastTouchx.value = motionEvent.x
                        lastTouchY.value = motionEvent.y

                        val temPath = path.value
                        path.value = null
                        path.value = temPath

                        //val pathMeasure = PathMeasure(temPath, false)
                        //val pathLength = pathMeasure.length

                        true
                    },
                onDraw = {

                    if (drawLine) {
                        drawLine(
                            start = Offset(x = positionXRect, y = positionYRect),
                            end = Offset(x = positionXRect + widthBox.value.toPx(), y = positionYRect),
                            strokeWidth = 5f,
                            color = Color.Red
                        )
                    }

                    Log.d("Prueba3", "$widthBox")

                    path.value?.let {
                        drawPath(
                            path = it,
                            color = Color.Black,
                            style = Stroke(
                                width = 4.dp.toPx()
                            )
                        )
                    }

                }
            )

            var offset by remember { mutableStateOf(Offset.Zero) }
            var scale by remember { mutableStateOf(1f) }
            var rotation by remember { mutableStateOf(0f) }

            val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                scale *= zoomChange
                rotation += rotationChange
                offset += offsetChange
            }

            var newScale by remember {
                mutableStateOf(1f)
            }
            val density = LocalDensity.current

            Box(
                Modifier
                    .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                    .widthIn(min = 200.dp, max = 1000.dp)
                    .size(width = widthBox.value, height = 100.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            offset += dragAmount

                        }
                        detectTransformGestures { _, pan, zoom, _ ->
                            // Scale the box based on the zoom gesture
                            newScale = (scale * zoom).coerceIn(0.1f, 10f)
                            scale = newScale

                            // Update the width variable based on the new scale
                            // widthBox = (with(LocalDensity.current) { widthBox.value.toPx() * newScale.toDp() }).coerceIn(200.dp, 1000.dp)
                        }
                        Log.d("Prueba", "${widthBox.value}")
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        rotationZ = rotation,
                        //translationX = offset.x
                        )
                    .transformable(state = state)
                    .background(Color.Blue)
                    .fillMaxSize()
                    .onGloballyPositioned {
                        positionXRect = it.positionInRoot().x
                        positionYRect = it.positionInRoot().y
                    }
                    .onSizeChanged {
                        Log.d("Prueba2", "${scale}")
                    }
            )

            Button(
                onClick = { drawLine = true }, shape = CutCornerShape(10)
            ) {
                Text(text = "Draw a Line")
            }
        }
    }
}

