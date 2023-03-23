package com.example.canvasruler

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
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
private fun ContentView(){

    val lastTouchx = remember{
        mutableStateOf(0f)
    }

    val lastTouchY = remember {
        mutableStateOf(0f)
    }

    val path = remember {
        mutableStateOf<Path?>(Path())
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ){


        Box(
            modifier = Modifier.fillMaxSize()
        ){

              Canvas(
                  modifier = Modifier
                      .fillMaxSize()
                      .pointerInteropFilter { motionEvent ->
                          when (motionEvent.action) {

                              MotionEvent.ACTION_DOWN -> {
                                  path.value?.moveTo(motionEvent.x, motionEvent.y)

                                  lastTouchx.value = motionEvent.x
                                  lastTouchY.value = motionEvent.y
                              }
                              MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {

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

                          true
                      },
                  onDraw ={

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


            Box(
                Modifier
                    .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                    .size(100.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            offset += dragAmount
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        rotationZ = rotation,
                    )
                    .transformable(state = state)
                    .background(Color.Blue)
                    .fillMaxSize()



            )

            Button(
                onClick = {  },shape = CutCornerShape(10)
            ) {
                Text(text = "Draw a Line")
            }





        }
    }
}

fun PintarLinea(){

}

