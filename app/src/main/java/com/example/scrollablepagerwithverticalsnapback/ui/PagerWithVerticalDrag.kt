package com.example.scrollablepagerwithverticalsnapback.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrollablepagerwithverticalsnapback.ui.theme.ScrollablePagerWithVerticalSnapBackTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun PagerWithVerticalDrag(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val screenHeightPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    val maxOffset = screenHeightPx / 2f

    var dragDirection by remember { mutableStateOf<DragDirection?>(null) }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    dragDirection = null

                    var pastTouchSlop = false
                    val touchSlop = viewConfiguration.touchSlop
                    val pointerId = down.id

                    drag(pointerId) { change ->
                        val dragAmount = change.positionChange()
                        if (!pastTouchSlop) {
                            if (dragAmount.getDistance() > touchSlop) {
                                dragDirection = if (abs(dragAmount.y) > abs(dragAmount.x)) {
                                    DragDirection.Vertical
                                } else {
                                    DragDirection.Horizontal
                                }
                                pastTouchSlop = true
                            }
                        }

                        if (dragDirection == DragDirection.Vertical) {
                            change.consume()
                            val newOffset = (offsetY.value + dragAmount.y)
                                .coerceIn(-maxOffset, maxOffset)

                            coroutineScope.launch {
                                offsetY.snapTo(newOffset)
                            }
                        }
                    }

                    // 指を離したら戻す（縦方向だった場合のみ）
                    if (dragDirection == DragDirection.Vertical) {
                        coroutineScope.launch {
                            offsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            )
                        }
                    }
                }
            }
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .background(
                    when (page % 3) {
                        0 -> Color.Red
                        1 -> Color.Green
                        else -> Color.Blue
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Page $page", color = Color.White, fontSize = 32.sp)
        }
    }
}

enum class DragDirection { Vertical, Horizontal }

@Preview(showBackground = true)
@Composable
fun PagerWithVerticalDragPreview() {
    ScrollablePagerWithVerticalSnapBackTheme {
        PagerWithVerticalDrag(Modifier.fillMaxSize())
    }
}
