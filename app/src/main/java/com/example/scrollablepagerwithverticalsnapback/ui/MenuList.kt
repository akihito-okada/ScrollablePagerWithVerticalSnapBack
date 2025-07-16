package com.example.scrollablepagerwithverticalsnapback.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuList(
    modifier: Modifier = Modifier,
    onItemClick: (MenuData) -> Unit,
) {
    Column(
        modifier,
    ) {
        MenuData.items.forEach { item ->
            MenuItem(item, onItemClick)
        }
    }
}

@Composable
fun MenuItem(
    item: MenuData,
    onItemClick: (MenuData) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
        onClick = { onItemClick(item) },
    ) {
        Text(
            text = item.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
        )
    }
}