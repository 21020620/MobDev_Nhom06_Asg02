package com.example.mobdev2.repo

import android.content.Context
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.Chapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single

@Single
class BookRepository(private val db: FirebaseFirestore) {

    suspend fun getBookByID(bookID: String): Book? {
        val bookDocument = db.collection("books").document(bookID).get().await()
        var book = bookDocument.toObject(Book::class.java)
        val chaptersCollection = bookDocument.reference.collection("chapters").get().await()
        val chapters = chaptersCollection.documents.mapNotNull { it.toObject(Chapter::class.java) }

        book?.chapters = chapters

        return book
    }
}