package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.AllBookRepo
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AllBookViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val allBookRepo: AllBookRepo
): ViewModel() {

    val bookList = savedStateHandle.getStateFlow("bookList", CachingResults.bookList)
    val isSearching = savedStateHandle.getStateFlow("isSearching", false)
    val searchString = savedStateHandle.getStateFlow("searchString", "")
    val listType = savedStateHandle.getStateFlow("listType", "All Books")

    suspend fun fetchBooks() {
        try {
            if(CachingResults.bookList.isEmpty()) {
                CachingResults.bookList = allBookRepo.getAllBooks()
            }
            savedStateHandle["bookList"] = CachingResults.bookList
        } catch (e: Exception) {
            Log.d("FETCH DATA FAILURE", "$e")
        }
    }

    fun updateSearchString(input: String) {
        savedStateHandle["searchString"] = input
    }

    fun updateSearchState() {
        savedStateHandle["isSearching"] = !isSearching.value
    }

    fun updateListType() {
        if(listType.value == "All Books") savedStateHandle["listType"] = "Recommendations"
        else savedStateHandle["listType"] = "All Books"
    }
}