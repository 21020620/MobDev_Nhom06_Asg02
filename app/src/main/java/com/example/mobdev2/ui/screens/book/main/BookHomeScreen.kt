package com.example.mobdev2.ui.screens.book.main

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.mobdev2.ui.screens.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun BookHomeScreen(
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }){ padding ->
        DestinationsNavHost(
            navController = navController,
            navGraph = NavGraphs.bookGraph,
        )
    }
}



