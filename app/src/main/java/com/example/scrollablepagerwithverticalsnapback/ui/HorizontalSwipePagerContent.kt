package com.example.scrollablepagerwithverticalsnapback.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun HorizontalSwipePagerContent(
    modifier: Modifier = Modifier,
) {
    var currentSetIndex by remember { mutableIntStateOf(0) }
    val mode by remember { mutableStateOf(SwipeMode.Vertical) }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

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

    SwipePagerContainer(
        modifier = modifier,
        mode = mode,
        currentSetIndex = currentSetIndex,
        onSwipeNext = {
            currentSetIndex = (currentSetIndex + 1) % 10
            coroutineScope.launch {
                pagerState.scrollToPage(0)
            }
        },
        onSwipePrevious = {
            currentSetIndex = (currentSetIndex - 1 + 10) % 10
            coroutineScope.launch {
                pagerState.scrollToPage(0)
            }
        },
        content = { setIndex ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, setIndex) }
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
    )
}