package com.example.mobdev2.ui.screens.book.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.repo.BookHomeRepo
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BookHomeScreenViewModel(
    private val navigator: DestinationsNavigator,
    private val bookHomeRepo: BookHomeRepo
) : ViewModel() {
    fun navigateToLogin() {
        navigator.navigate(LoginScreenDestination) {
            popUpTo(NavGraphs.root)
        }
    }

    fun setCurrentUser() = viewModelScope.launch {
        bookHomeRepo.setCurrentUser()
    }
}