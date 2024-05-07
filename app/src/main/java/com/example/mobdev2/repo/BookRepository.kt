package com.example.mobdev2.repo

import android.util.Log
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.Chapter
import com.example.mobdev2.repo.model.ReaderData
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

@Single
class ReaderDataRepository(private val db: FirebaseFirestore) {
    suspend fun saveReaderData(readerData: ReaderData) {
        val documentPath = "users/${readerData.userID}/books/${readerData.bookID}"

        try {
            db.document(documentPath).set(readerData).await()
        } catch (e: Exception) {
            Log.e("ERROR SAVING READER DATA", "$e")
        }
    }

    suspend fun getReaderData(userID: String, bookID: String):ReaderData? {
        val documentPath = "users/$userID/books/$bookID"
        return try {
            val snapshot = db.document(documentPath).get().await()
            snapshot.toObject(ReaderData::class.java)
        } catch (e: Exception) {
            Log.e("ERROR FETCHING READER DATA", "$e")
            null
        }
    }

    suspend fun updateReaderData(userID: String, bookID: String, updates: Map<String, Any>) {
        val documentPath = "users/$userID/books/$bookID"
        try {
            db.document(documentPath).update(updates).await()
        } catch (e: Exception) {
            Log.e("ERROR UPDATING READER DATA", "$e")
        }
    }
}