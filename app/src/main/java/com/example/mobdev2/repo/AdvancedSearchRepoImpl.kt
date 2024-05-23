package com.example.mobdev2.repo

import android.util.Log
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.model.Chapter
import com.example.mobdev2.repo.model.ReaderData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single

@Single
class AdvancedSearchRepoImpl(
    private val db: FirebaseFirestore
): AdvancedSearchRepo {
    override suspend fun getChaptersOfBook(bookID: String): List<Chapter> {
        val chaptersCollection =
            db.collection("books").document(bookID).collection("chapters").get()
                .await()
        return chaptersCollection.documents.mapNotNull { it.toObject(Chapter::class.java) }
    }

    override suspend fun saveReaderData(readerData: ReaderData) {
        val documentPath = "users/${readerData.userID}/books/${readerData.bookID}"

        try {
            db.document(documentPath).set(readerData).await()
        } catch (e: Exception) {
            Log.e("ERROR SAVING READER DATA", "$e")
        }
    }

    override suspend fun updateReaderData(userID: String, bookID: String, updates: Map<String, Any>) {
        val documentPath = "users/$userID/books/$bookID"
        try {
            db.document(documentPath).update(updates).await()
        } catch (e: Exception) {
            Log.e("ERROR UPDATING READER DATA", "$e")
        }
    }

    override suspend fun getReaderData(userID: String, bookID: String):ReaderData? {
        val documentPath = "users/$userID/books/$bookID"
        return try {
            val snapshot = db.document(documentPath).get().await()
            snapshot.toObject(ReaderData::class.java)
        } catch (e: Exception) {
            Log.e("ERROR FETCHING READER DATA", "$e")
            null
        }
    }
}