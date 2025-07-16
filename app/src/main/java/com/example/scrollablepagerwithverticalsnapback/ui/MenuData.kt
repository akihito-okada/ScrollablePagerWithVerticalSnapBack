package com.example.scrollablepagerwithverticalsnapback.ui

sealed class MenuData(val title: String) {
    class VerticalPager : MenuData("Vertical Pager")
    class HorizontalPager : MenuData("Horizontal Pager")

    companion object {

        val items by lazy {
            listOf(
                HorizontalPager(),
                VerticalPager(),
            )
        }
    }
}