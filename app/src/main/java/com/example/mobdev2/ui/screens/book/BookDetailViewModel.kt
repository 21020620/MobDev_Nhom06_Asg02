package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.repo.BookRepository
import com.example.mobdev2.repo.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class BookDetailViewModel(
    private val bookRepo:BookRepository,
    private val savedStateHandle: SavedStateHandle,
):ViewModel() {
    var book by mutableStateOf<Book?>(null)
        private set
    init {
        book = savedStateHandle.get<Book>("book")
    }
    fun getBookDetails(bookID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedBook = bookRepo.getBookByID(bookID)
                savedStateHandle["book"] = fetchedBook
                book = fetchedBook
            } catch (e: Exception) {
                Log.e("FETCH DATA FAILURE", "$e")
            }
        }

    }
}