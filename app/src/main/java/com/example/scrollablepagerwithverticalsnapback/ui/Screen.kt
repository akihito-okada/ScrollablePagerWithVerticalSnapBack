package com.example.scrollablepagerwithverticalsnapback.ui

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object VerticalPager : Screen("vertical_pager")
    object HorizontalPager : Screen("horizontal_pager")
}