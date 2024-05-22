package com.example.mobdev2.ui.screens.book.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobdev2.repo.Authenticator
import com.example.mobdev2.ui.screens.NavGraphs
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination
@BookNavGraph
@Composable
fun BookHomeScreen(
    navigator: DestinationsNavigator,
    authenticator: Authenticator = get(),
    viewModel: BookHomeScreenViewModel = koinViewModel(parameters = {
        parametersOf(navigator)
    })
) {
    val navController = rememberNavController()

    val authState = remember { mutableStateOf(authenticator.currentUser()) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener {
            authState.value = it.currentUser
        }
        authenticator.addAuthStateListener(listener)

        onDispose {
            authenticator.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(authState.value) {
        if (authState.value == null) {
            viewModel.navigateToLogin()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setCurrentUser()
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry.value?.destination

            if(arrayOf("all_book_screen", "advanced_search_screen", "settings_screen", "library_screen").contains(currentDestination?.route)) {
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



