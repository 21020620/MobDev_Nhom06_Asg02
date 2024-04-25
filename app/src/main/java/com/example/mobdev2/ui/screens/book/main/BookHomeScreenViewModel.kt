package com.example.mobdev2.ui.screens.book.main

import androidx.lifecycle.ViewModel
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BookHomeScreenViewModel(
    private val navigator: DestinationsNavigator
) : ViewModel() {
    fun navigateToLogin() {
        navigator.navigate(LoginScreenDestination) {
            popUpTo(NavGraphs.root)
        }
    }
}