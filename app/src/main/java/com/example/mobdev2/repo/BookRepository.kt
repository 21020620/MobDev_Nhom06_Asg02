package com.example.mobdev2.repo

import android.util.Log
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.Chapter
import com.example.mobdev2.repo.model.ReaderData
import com.example.mobdev2.repo.model.Reply
import com.example.mobdev2.repo.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single

@Single
class BookRepository(private val db: FirebaseFirestore) {

    suspend fun getBookByID(bookID: String): Book? {
        val bookDocument = db.collection("books").document(bookID).get().await()
        val book = bookDocument.toObject(Book::class.java)
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

@Single
class ReviewRepository(private val db: FirebaseFirestore) {
    suspend fun saveReviewData(bookID: String, reviewData: Review, reviewID: Int) {
        val documentPath = "books/${bookID}/reviews/${reviewID}"

        try {
            db.document(documentPath).set(reviewData).await()
        } catch (e: Exception) {
            Log.e("ERROR SAVING REVIEW DATA", "$e")
        }
    }

    suspend fun addReplyData(bookID: String, replyData: Reply, reviewID: Int) {
        val documentPath = "books/${bookID}/reviews/${reviewID}/replies"

        try {
            db.collection(documentPath).add(replyData).await()
        } catch (e: Exception) {
            Log.e("ERROR SAVING REPLY DATA", "$e")
        }
    }

    suspend fun getReviewsData(bookID: String): List<Review>? {
        val collectionPath = "books/$bookID/reviews"
        return try {
            val snapshot = db.collection(collectionPath).get().await()
            val reviews = snapshot.documents.mapNotNull { document ->
                document.toObject(Review::class.java)?.copy(reviewID = document.id.toIntOrNull() ?: -1)
            }
            reviews
        } catch (e: Exception) {
            Log.e("ERROR FETCHING REVIEWS DATA", "$e")
            null
        }
    }

    suspend fun getRepliesData(bookID: String, reviewID: Int): List<Reply>? {
        val collectionPath = "books/${bookID}/reviews/${reviewID}/replies"
        return try {
            val snapshot = db.collection(collectionPath).get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Reply::class.java)
            }
        } catch (e: Exception) {
            Log.e("ERROR FETCHING REPLIES DATA", "$e")
            null
        }
    }

    suspend fun getReviewData(bookID: String, reviewID: Int): Review? {
        val documentPath = "books/${bookID}/reviews/${reviewID}"
        return try {
            val snapshot = db.document(documentPath).get().await()
            snapshot.toObject(Review::class.java)?.copy(reviewID = reviewID)
        } catch (e: Exception) {
            Log.e("ERROR FETCHING REVIEW DATA", "$e")
            null
        }
    }

    suspend fun updateReviewLikes(bookID: String, reviewID: Int, likesCount: Int) {
        val reviewRef = db.collection("books").document(bookID).collection("reviews").document(reviewID.toString())
        reviewRef.update("likesCount", likesCount).await()
    }

    suspend fun updateReplyLikes(bookID: String, reviewID: Int, replyID: Int, likesCount: Int) {
        val replyRef = db.collection("books").document(bookID).collection("reviews")
            .document(reviewID.toString()).collection("replies").document(replyID.toString())
        replyRef.update("likesCount", likesCount).await()
    }
}