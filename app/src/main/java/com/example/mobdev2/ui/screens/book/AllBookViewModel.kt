package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.mobdev2.CachingResults
import com.example.mobdev2.model.Book
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

    suspend fun fetchBooks() {
        try {
            if(CachingResults.bookList.isEmpty()) {
                val snapshot = db.collection("books")
                    .get()
                    .await()

                CachingResults.bookList = snapshot.documents.map {
                    it.toObject<Book>()!!
                }
            }
            savedStateHandle["bookList"] = CachingResults.bookList
        } catch (e: Exception) {
            Log.d("FETCH DATA FAILURE", "$e")
        }
    }
}