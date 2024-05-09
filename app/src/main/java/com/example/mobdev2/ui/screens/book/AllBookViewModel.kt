package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.CachingResults
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AllBookViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val db: FirebaseFirestore
): ViewModel() {

    val bookList = savedStateHandle.getStateFlow("bookList", CachingResults.bookList)
    val isSearching = savedStateHandle.getStateFlow("isSearching", false)
    val searchString = savedStateHandle.getStateFlow("searchString", "")
    val listType = savedStateHandle.getStateFlow("listType", "All Books")

    suspend fun fetchBooks() {
        try {
            if(CachingResults.bookList.isEmpty()) {
                val snapshot = db.collection("books")
                    .get()
                    .await()

                CachingResults.bookList = snapshot.documents.map {
                    val book = it.toObject<Book>()!!
                    book.copy(id = it.id)
                }
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
        if(listType.value == "All Books") savedStateHandle["listType"] = "Recommended for you"
        else savedStateHandle["listType"] = "All Books"
    }
}