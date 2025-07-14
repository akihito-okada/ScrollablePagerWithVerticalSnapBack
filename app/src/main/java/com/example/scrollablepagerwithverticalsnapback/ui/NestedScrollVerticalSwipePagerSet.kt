package com.example.scrollablepagerwithverticalsnapback.ui

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrollablepagerwithverticalsnapback.ui.theme.ScrollablePagerWithVerticalSnapBackTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun NestedScrollVerticalSwipePagerSet(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

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

    var currentSetIndex by remember { mutableIntStateOf(0) }

    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (abs(available.y) > abs(available.x)) {
                    coroutineScope.launch {
                        val newOffset =
                            (offsetY.value + available.y).coerceIn(-maxOffset, maxOffset)
                        offsetY.snapTo(newOffset)
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (offsetY.value > threshold) {
                    currentSetIndex = (currentSetIndex - 1 + colorSets.size) % colorSets.size
                } else if (offsetY.value < -threshold) {
                    currentSetIndex = (currentSetIndex + 1) % colorSets.size
                }
                offsetY.animateTo(0f, animationSpec = tween(300))
                return Velocity.Zero
            }
        }
    }

    // 👉 セット切替後の処理を安定して行う
    LaunchedEffect(currentSetIndex) {
        Toast.makeText(context, "Set ${currentSetIndex + 1}", Toast.LENGTH_SHORT).show()
        pagerState.scrollToPage(0)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection, nestedScrollDispatcher)
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
                .scrollable(
                    orientation = Orientation.Vertical,
                    state = rememberScrollableState { delta ->
                        coroutineScope.launch {
                            val newOffset = (offsetY.value + delta).coerceIn(-maxOffset, maxOffset)
                            offsetY.snapTo(newOffset)
                        }
                        delta
                    }
                )
                .nestedScroll(nestedScrollConnection, nestedScrollDispatcher)
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
        ) { page ->
            val backgroundColor = colorSets
                .getOrNull(currentSetIndex)
                ?.getOrNull(page)
                ?: Color.Black

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
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
        NestedScrollVerticalSwipePagerSet(Modifier.fillMaxSize())
    }
}
