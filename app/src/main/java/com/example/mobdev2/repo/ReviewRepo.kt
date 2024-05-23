package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Reply
import com.example.mobdev2.repo.model.Review

interface ReviewRepo {
    suspend fun saveReviewData(bookID: String, reviewData: Review, reviewID: Int)

    suspend fun addReplyData(bookID: String, replyData: Reply, reviewID: Int)

    suspend fun getReviewsData(bookID: String): List<Review>?

    suspend fun getRepliesData(bookID: String, reviewID: Int): List<Reply>?

    suspend fun getReviewData(bookID: String, reviewID: Int): Review?

}