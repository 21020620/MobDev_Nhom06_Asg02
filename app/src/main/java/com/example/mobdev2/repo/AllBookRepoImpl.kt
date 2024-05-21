package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class AllBookRepoImpl(
    private val db: FirebaseFirestore
): AllBookRepo {
    override suspend fun getAllBooks(): List<Book> {
        val snapshot = db.collection("books").get().await()
        return snapshot.documents.map {
            val book = it.toObject<Book>()!!
            book.copy(id = it.id)
        }
    }
}