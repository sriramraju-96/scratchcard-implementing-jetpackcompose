package com.example.scratchcard.ui

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.scratchcard.R
import com.example.scratchcard.model.DragPath


@Composable
fun ScratchCard() {
    val overlayImage = ImageBitmap.imageResource(id = R.drawable.overlay_image)
    val baseImage = ImageBitmap.imageResource(id = R.drawable.base_image)

    var currentPath by remember { mutableStateOf(DragPath(path = Path())) }
    var movedOffSet by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IconButton(onClick = {
            movedOffSet = null
            currentPath = DragPath(path = Path())
        }, modifier = Modifier.align(Alignment.TopCenter)) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "clear content",

                )

        }

        Scratching(
            overlayImage = overlayImage,
            baseImage = baseImage,
            modifier = Modifier.align(Alignment.Center),
            movedOffSet = movedOffSet,
            onMovedOffset = { x, y ->
                movedOffSet = Offset(x, y)
            },
            currentPath = currentPath.path,
            currentPathThickness = currentPath.width
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Scratching(
    overlayImage: ImageBitmap,
    baseImage: ImageBitmap,
    modifier: Modifier,
    movedOffSet: Offset?,
    onMovedOffset: (Float, Float) -> Unit,
    currentPath: Path,
    currentPathThickness: Float
) {

    Canvas(
        modifier = modifier
            .clipToBounds()
            .clip(RoundedCornerShape(20.dp))
            .size(200.dp)

            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_MOVE -> {
                        onMovedOffset(it.x, it.y)
                    }
                }
                true
            }
    ) {
        val canvasHeight = size.height.toInt()
        val canvasWidth = size.width.toInt()

        val imageSize = IntSize(width = canvasWidth, height = canvasHeight)

        drawImage(
            image = overlayImage,
            dstSize = imageSize
        )

        movedOffSet?.let {
            currentPath.addOval(oval = Rect(it, currentPathThickness))

        }
        clipPath(path = currentPath, ClipOp.Intersect) {
            drawImage(
                image = baseImage,
                dstSize = imageSize
            )
        }
    }

}