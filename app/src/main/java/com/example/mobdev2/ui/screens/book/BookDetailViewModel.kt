package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.Book2Repo
import com.example.mobdev2.repo.BookRepo
import com.example.mobdev2.repo.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class BookDetailViewModel(
    private val bookRepo: BookRepo,
    private val book2Repo: Book2Repo,
    private val savedStateHandle: SavedStateHandle,
):ViewModel() {
    var book by mutableStateOf<Book?>(null)
        private set
    init {
        book = savedStateHandle.get<Book>("book")
    }

    var similarBooks by mutableStateOf(listOf<Book>())

    var isLoading by mutableStateOf(true)

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

    fun getSimilarBooks(bookID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedBook = bookRepo.getSimilarBooks(bookID)
                fetchedBook?.let {
                    similarBooks = it
                }

            } catch (e: Exception) {
                Log.e("FETCH DATA FAILURE", "$e")
            }
        }
    }

    fun addBookToLib(bookID: String) {
        viewModelScope.launch {
            book2Repo.addBookToLibrary(bookID)
        }
        CachingResults.currentUser = CachingResults.currentUser.copy(library = CachingResults.currentUser.library + bookID)
    }

    fun loadHighlight(bookID: String) = viewModelScope.launch {
        try {
            val stringList = book2Repo.loadHighlight(bookID)
            CachingResults.highlights = stringList.map {
                if(it.isNotBlank()) {
                    it.split(",").map { num ->
                        num.toInt()
                    }
                } else {
                    listOf()
                }
            }
            Log.d("CACHING RESULT", CachingResults.highlights.joinToString("/"))
            isLoading = false
        } catch (e: Exception) {
            Log.d("HIGHLIGHT", "NO HIGHLIGHT: $e")
            isLoading = false
        }
    }
}