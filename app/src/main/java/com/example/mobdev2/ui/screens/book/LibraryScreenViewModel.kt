package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.CachingResults
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LibraryScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val db: FirebaseFirestore
): ViewModel(){
    val libraryList = savedStateHandle.getStateFlow("libraryList", CachingResults.currentUser.library)

    fun deleteFromLibrary(id: String) {
        savedStateHandle["libraryList"] = libraryList.value.filterNot {
            it == id
        }
        CachingResults.currentUser = CachingResults.currentUser.copy(library = CachingResults.currentUser.library.filterNot { it == id })
        viewModelScope.launch(Dispatchers.IO) {
            val userID = CachingResults.currentUser.name
            val updates = hashMapOf<String, Any>(
                "library" to CachingResults.currentUser.library.filterNot {
                    it == id
                }
            )

            db.collection("users").document(userID)
                .update(updates)
                .addOnSuccessListener {
                    Log.d("UPDATE LIBRARY", "SUCCESS REMOVED")
                }
                .addOnFailureListener { e ->
                    Log.d("UPDATE LIBRARY", "REMOVED FAIL: $e")
                }
        }
    }
}