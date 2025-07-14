package com.example.scrollablepagerwithverticalsnapback.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
fun VerticalSwipePagerSet(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    val screenHeightPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    val threshold = with(LocalDensity.current) { 100.dp.toPx() }
    val maxOffset = threshold + 1

    val colorSets = remember {
        List(10) { setIndex ->
            List(3) { pageIndex ->
                Color(
                    red = (50 + 20 * setIndex + pageIndex * 10) % 256,
                    green = (100 + 30 * setIndex + pageIndex * 20) % 256,
                    blue = (150 + 40 * setIndex + pageIndex * 30) % 256
                )
            }
        }
    }

    var currentSetIndex by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var dragDirection: DragDirection? = null
                    var totalDrag = Offset.Zero
                    val touchSlop = viewConfiguration.touchSlop

                    drag(down.id) { change ->
                        val dragAmount = change.positionChange()
                        totalDrag += dragAmount

                        if (dragDirection == null && totalDrag.getDistance() > touchSlop) {
                            dragDirection = if (abs(totalDrag.y) > abs(totalDrag.x)) {
                                DragDirection.Vertical
                            } else {
                                DragDirection.Horizontal
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

                    if (dragDirection == DragDirection.Vertical) {
                        coroutineScope.launch {
                            if (offsetY.value > threshold) {
                                currentSetIndex =
                                    (currentSetIndex - 1 + colorSets.size) % colorSets.size
                                pagerState.scrollToPage(0)
                            } else if (offsetY.value < -threshold) {
                                currentSetIndex = (currentSetIndex + 1) % colorSets.size
                                pagerState.scrollToPage(0)
                            }
                            offsetY.animateTo(0f, animationSpec = tween(300))
                        }
                    }
                }
            }
    ) {
        Text(
            text = "↑ 前の作品",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(
            text = "↓ 次の作品",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            fontSize = 16.sp,
            color = Color.Gray
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorSets[currentSetIndex][page]),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Page $page\nSet ${currentSetIndex + 1}",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerticalSwipePagerSetPreview() {
    ScrollablePagerWithVerticalSnapBackTheme {
        VerticalSwipePagerSet(Modifier.fillMaxSize())
    }
}
