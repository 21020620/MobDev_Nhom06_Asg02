package com.example.mobdev2.ui.screens.book.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
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
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry.value?.destination

            if(arrayOf("all_book_screen", "book_forum_screen", "settings_screen", "library_screen").contains(currentDestination?.route)) {
                BottomBar(navController)
            }
        }){ padding ->
        Box(modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.bookGraph,
            )
        }
    }
}



