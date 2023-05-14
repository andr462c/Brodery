package me.andr4.android.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.*
import me.andr4.android.Project
import kotlin.math.roundToInt
import kotlin.math.max
import kotlin.math.min


@Composable
fun projectView(
    project: Project,
    modifier: Modifier = Modifier
) {
    val imageBitmap = project.getBitmap()?.asImageBitmap() ?: return
    var zoom by remember { mutableStateOf(50f) }
    var zoomInt = zoom.toInt()
    val pixelMap = imageBitmap.toPixelMap()

    //Offset of picture
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var roffsetX by remember { mutableStateOf(0f) }
    var roffsetY by remember { mutableStateOf(0f) }
    var panX by remember { mutableStateOf(0f) }
    var panY by remember { mutableStateOf(0f) }

    Canvas(modifier = modifier
        .padding(10.dp)
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { centroid, pan, scale, _ ->
                offsetX += pan.x
                offsetY += pan.y
                //zoom
                var newZoom = zoom * scale
                if (newZoom <= 1) newZoom = 1f

                val difX = offsetX - centroid.x
                val difY = offsetY - centroid.y
                val zoomFactor = (newZoom - zoom)/zoom
                offsetX += difX*zoomFactor
                offsetY += difY*zoomFactor

                zoom = newZoom

/*
                intScale
                // If a change in zoom happens Update zoom and offset
                if (zoomInt != zoom.toInt()) {
                    val difX = offsetX - centroid.x
                    val difY = offsetY - centroid.y
                    val zoomFactor = (zoom.toInt() - zoomInt)/zoomInt.toFloat()
                    offsetX = difX * zoomFactor
                    offsetY = difY * zoomFactor

                    zoomInt = zoom.toInt()
                }
 */
            }
        }
        .graphicsLayer(alpha = 0.99f)) {
        drawRect(
            color = Color.LightGray,
            size = size,
        )
        val topLeft = getOffsetOfPixel(0, 0, offsetX, offsetY, zoom)
        drawImage(
            image = imageBitmap,
            dstSize = IntSize((imageBitmap.width * zoom).toInt(), (imageBitmap.height * zoom).toInt()),
            dstOffset = IntOffset(topLeft.x.toInt(), topLeft.y.toInt()),
            filterQuality = FilterQuality.None,

        )
        //Draw grid
        val gridColor = Color.White
        val xInScreen = (size.width/zoom).toInt() +2
        val yInScreen = (size.height/zoom).toInt() +2
        val xStartInView = max(-(offsetX/zoom).toInt(),0)
        val yStartInView = max(-(offsetY/zoom).toInt(),0)
        val xEndInView = min(xStartInView + xInScreen,pixelMap.width)
        val yEndInView = min(yStartInView + yInScreen,pixelMap.height)

        if (zoom > 30) {

            for (x in xStartInView until xEndInView) {
                val pos = getOffsetOfPixel(x, yStartInView, offsetX, offsetY, zoom)
                val height = min(yInScreen, pixelMap.height-yStartInView)
                drawLine(
                    start = pos.plus(Offset(0f,zoom * height)),
                    end = pos,
                    color = gridColor,
                    strokeWidth = 1f,
                    blendMode = BlendMode.Difference
                )
                drawLine(
                    start = pos.plus(Offset(0f,zoom * height)),
                    end = pos,
                    color = gridColor,
                    strokeWidth = 1f,
                    alpha = 0.3f
                )
            }

            for (y in yStartInView until yEndInView) {
                val pos = getOffsetOfPixel(xStartInView, y, offsetX, offsetY, zoom)
                val width = min(xInScreen, pixelMap.width-xStartInView)
                drawLine(
                    start = pos.plus(Offset(zoom * width, 0f)),
                    end = pos,
                    strokeWidth = 2f,
                    color = gridColor,
                    blendMode = BlendMode.Difference
                )
                drawLine(
                    start = pos.plus(Offset(zoom * width, 0f)),
                    end = pos,
                    strokeWidth = 2f,
                    color = Color.Black,
                    alpha = 0.3f
                )
            }
        }
    }
}

fun bitmapView(drawScope: DrawScope, pixelMap: PixelMap, offsetX: Float, offsetY: Float, zoomInt: Int) {
    drawScope.let {
        val gridColor = Color.Black
        val xInScreen = (it.size.width/zoomInt.toFloat()).toInt() +2
        val yInScreen = (it.size.height/zoomInt.toFloat()).toInt() +2
        val xStartInView = max(-(offsetX/zoomInt.toFloat()).toInt(),0)
        val yStartInView = max(-(offsetY/zoomInt.toFloat()).toInt(),0)
        val xEndInView = min(xStartInView + xInScreen,pixelMap.width)
        val yEndInView = min(yStartInView + yInScreen,pixelMap.height)
        for (x in xStartInView until xEndInView) {
            for (y in yStartInView until yEndInView) {

                val topLeft = getOffsetOfPixel(x, y, offsetX, offsetY, zoomInt.toFloat())
                it.drawRect(
                    topLeft = topLeft,
                    size = IntSize(zoomInt, zoomInt).toSize(),
                    color = pixelMap[x, y],
                )
            }
        }
        val topLeft = getOffsetOfPixel(0, 0, offsetX, offsetY, zoomInt.toFloat())
        if (zoomInt > 4) {
            for (x in 0 until pixelMap.width+1) {
                it.drawLine(
                    start = topLeft.plus(IntOffset(zoomInt * x, 0)),
                    end = topLeft.plus(IntOffset(zoomInt * x, zoomInt * pixelMap.width)),
                    color = gridColor
                )
            }
            for (y in 0 until pixelMap.height+1) {
                it.drawLine(
                    start = topLeft.plus(IntOffset(0, zoomInt * y)),
                    end = topLeft.plus(IntOffset(zoomInt * pixelMap.height,zoomInt * y)),
                    color = gridColor
                )

            }
        }
    }

}

fun getOffsetOfPixel(posX: Int, posY: Int, offsetX: Float, offsetY: Float, zoom: Float): Offset {
    return IntOffset((offsetX + posX * (zoom)).toInt(),(offsetY + posY * (zoom)).toInt()).toOffset()
}