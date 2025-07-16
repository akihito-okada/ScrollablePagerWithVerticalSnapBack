package com.example.scrollablepagerwithverticalsnapback.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


enum class SwipeMode {
    Vertical,
    Horizontal
}

@Composable
fun SwipePagerContainer(
    mode: SwipeMode,
    currentSetIndex: Int,
    onSwipeNext: () -> Unit,
    onSwipePrevious: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (setIndex: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offset = remember { Animatable(0f) }
    val threshold = with(LocalDensity.current) { 100.dp.toPx() }
    val maxOffset = threshold + 1f

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(mode) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var totalDrag = Offset.Zero
                    var dragDirection: DragDirection? = null
                    val touchSlop = viewConfiguration.touchSlop

                    drag(down.id) { change ->
                        val dragAmount = change.positionChange()
                        totalDrag += dragAmount

                        if (dragDirection == null && totalDrag.getDistance() > touchSlop) {
                            dragDirection = if (abs(dragAmount.y) > abs(dragAmount.x)) {
                                DragDirection.Vertical
                            } else {
                                DragDirection.Horizontal
                            }
                        }

                        if (
                            (mode == SwipeMode.Vertical && dragDirection == DragDirection.Vertical) ||
                            (mode == SwipeMode.Horizontal && dragDirection == DragDirection.Horizontal)
                        ) {
                            change.consume()
                            val delta =
                                if (mode == SwipeMode.Vertical) dragAmount.y else dragAmount.x
                            val newOffset = (offset.value + delta).coerceIn(-maxOffset, maxOffset)
                            coroutineScope.launch {
                                offset.snapTo(newOffset)
                            }
                        }
                    }

                    if (
                        (mode == SwipeMode.Vertical && dragDirection == DragDirection.Vertical) ||
                        (mode == SwipeMode.Horizontal && dragDirection == DragDirection.Horizontal)
                    ) {
                        coroutineScope.launch {
                            when {
                                offset.value > threshold -> onSwipePrevious()
                                offset.value < -threshold -> onSwipeNext()
                            }
                            offset.animateTo(0f, tween(300))
                        }
                    }
                }
            }
    ) {
        // 背景ラベル（Vertical）
        if (mode == SwipeMode.Vertical) {
            if (offset.value > 0f) {
                Text(
                    "↑ 前の作品",
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    color = Color.Gray
                )
            }
            if (offset.value < 0f) {
                Text(
                    "↓ 次の作品",
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    color = Color.Gray
                )
            }
        }

        // 背景ラベル（Horizontal）
        if (mode == SwipeMode.Horizontal) {
            if (offset.value > 0f) {
                Text(
                    "← 前の作品",
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp),
                    color = Color.Gray
                )
            }
            if (offset.value < 0f) {
                Text(
                    "次の作品 →",
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    color = Color.Gray
                )
            }
        }

        // 中身（offset 適用方向を mode によって切替）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    when (mode) {
                        SwipeMode.Vertical ->
                            it.offset { IntOffset(0, offset.value.roundToInt()) }

                        SwipeMode.Horizontal ->
                            it.offset { IntOffset(offset.value.roundToInt(), 0) }
                    }
                }
        ) {
            content(currentSetIndex)
        }
    }
}
