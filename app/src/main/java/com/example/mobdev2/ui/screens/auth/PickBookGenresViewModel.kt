package com.example.mobdev2.ui.screens.auth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.R
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.BookHomeScreenDestination
import com.google.firebase.firestore.FirebaseFirestore
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PickBookGenresViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val navigator: DestinationsNavigator,
    private val db: FirebaseFirestore
): ViewModel() {
    val pickedList = savedStateHandle.getStateFlow("genres", List(10){ false })

    val bookGenreList: ArrayList<BookGenre> = arrayListOf(
        BookGenre(R.drawable.art_books, "Art Books"),
        BookGenre(R.drawable.history_books, "Historical Fiction"),
        BookGenre(R.drawable.biographies, "Biographies"),
        BookGenre(R.drawable.kids_books, "Kids Books"),
        BookGenre(R.drawable.romance, "Romance"),
        BookGenre(R.drawable.medical_books, "Medical Books"),
        BookGenre(R.drawable.mystery, "Mystery"),
        BookGenre(R.drawable.science_fiction, "Science Fiction"),
        BookGenre(R.drawable.science_books, "Science Books"),
        BookGenre(R.drawable.fantasy, "Fantasy"),
    )

    fun setPickedList(idx: Int) {
        savedStateHandle["genres"] = pickedList.value.mapIndexed { index, item ->
            if (index == idx) !item else item
        }
    }


    fun navigateToHome(email: String) = viewModelScope.launch{
        val pickedGenres = bookGenreList.filterIndexed { index, _ ->
            pickedList.value[index]
        }.map { it.name }

        launch {
            val newUser = hashMapOf<String, Any>(
                "favouriteTags" to pickedGenres,
                "library" to listOf<String>()
            )
            try {
                db.collection("users")
                    .document(email.substringBefore("@"))
                    .set(newUser)
                    .await()
            } catch (e: Exception) {
                Log.d("ADD USER", "FAILURE")
            }
        }

        navigator.navigate(BookHomeScreenDestination) {
            popUpTo(NavGraphs.root)
        }
    }
}
