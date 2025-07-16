package com.example.scrollablepagerwithverticalsnapback.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun VerticalSwipePagerContent(
    modifier: Modifier = Modifier,
) {
    var currentSetIndex by remember { mutableIntStateOf(0) }
    val mode by remember { mutableStateOf(SwipeMode.Horizontal) }
    val scrollState = rememberScrollState()
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
                scrollState.scrollTo(0)
            }
        },
        onSwipePrevious = {
            currentSetIndex = (currentSetIndex - 1 + 10) % 10
            coroutineScope.launch {
                scrollState.scrollTo(0)
            }
        },
        content = { setIndex ->
            val color = Color((setIndex * 30) % 256, 120, 200)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(color),
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("タイトル $currentSetIndex", fontSize = 28.sp, color = Color.White)
                }
                val screenHeightDp = with(LocalConfiguration.current) { screenHeightDp.dp }
                repeat(3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeightDp)
                            .background(colorSets[setIndex][it]),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxSize(),
                            text = "Line $it",
                            color = Color.White,
                        )
                    }
                }
            }
        }
    )
}