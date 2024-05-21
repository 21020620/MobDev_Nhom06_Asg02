package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.BookRepository
import com.example.mobdev2.repo.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class BookDetailViewModel(
    private val bookRepo:BookRepository,
    private val savedStateHandle: SavedStateHandle,
    private val db: FirebaseFirestore
):ViewModel() {
    var book by mutableStateOf<Book?>(null)
        private set
    init {
        book = savedStateHandle.get<Book>("book")
    }

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

    fun addBookToLib(bookID: String) = viewModelScope.launch(Dispatchers.IO) {
        val userID = CachingResults.currentUser.name
        val updates = hashMapOf<String, Any>(
            "library" to CachingResults.currentUser.library + bookID
        )

        CachingResults.currentUser = CachingResults.currentUser.copy(library = CachingResults.currentUser.library + bookID)

        db.collection("users").document(userID)
            .update(updates)
            .addOnSuccessListener {
                Log.d("UPDATE LIBRARY", "SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.d("UPDATE LIBRARY", "FAIL: $e")
            }
    }

    fun loadHighlight(bookID: String) {
        db.collection("users")
            .document(CachingResults.currentUser.name)
            .collection("books")
            .document(bookID)
            .get()
            .addOnSuccessListener { document ->
                try {
                    val stringList = document.get("highlights") as List<String>
                    CachingResults.highlights = stringList.map {
                        if(!it.isNullOrBlank()) {
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
            .addOnFailureListener {
                Log.d("GET HIGHLIGHT FAILED", "$it")
            }
    }
}