package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.LibraryRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LibraryScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val libraryRepo: LibraryRepo
): ViewModel(){
    val libraryList = savedStateHandle.getStateFlow("libraryList", CachingResults.currentUser.library)

    fun deleteFromLibrary(id: String) {
        savedStateHandle["libraryList"] = libraryList.value.filterNot {
            it == id
        }
        CachingResults.currentUser = CachingResults.currentUser.copy(library = CachingResults.currentUser.library.filterNot { it == id })
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepo.removeBookFromLibrary(id)
            } catch (e: Exception) {
                Log.d("LIBRARY EXCEPTION", "$e")
            }
        }
    }
}