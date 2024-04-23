package com.example.mobdev2.ui.screens.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.BookHomeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PickBookGenresViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val navigator: DestinationsNavigator
): ViewModel() {
    val pickedList = savedStateHandle.getStateFlow("genres", List(10){false})

    fun setPickedList(idx: Int) {
        savedStateHandle["genres"] = pickedList.value.mapIndexed { index, item ->
            if (index == idx) !item else item
        }
    }

    fun navigateToHome() = viewModelScope.launch{
        navigator.navigate(BookHomeScreenDestination) {
            popUpTo(NavGraphs.root)
        }
    }
}
