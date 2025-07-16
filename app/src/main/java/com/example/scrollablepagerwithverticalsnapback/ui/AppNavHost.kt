package com.example.scrollablepagerwithverticalsnapback.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(Screen.Menu.route) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("メニュー") })
                }
            ) { innerPadding ->
                MenuList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onItemClick = { item ->
                        when (item) {
                            is MenuData.VerticalPager -> navController.navigate(Screen.VerticalPager.route)
                            is MenuData.HorizontalPager -> navController.navigate(Screen.HorizontalPager.route)
                        }
                    }
                )
            }
        }

        composable(Screen.VerticalPager.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Vertical Pager") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                VerticalSwipePagerContent(modifier = Modifier.padding(innerPadding))
            }
        }

        composable(Screen.HorizontalPager.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Horizontal Pager") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                HorizontalSwipePagerContent(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}