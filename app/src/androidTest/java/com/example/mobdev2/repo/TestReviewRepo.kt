package com.example.mobdev2.repo

import com.example.mobdev2.repo.model.Reply
import com.example.mobdev2.repo.model.Review
import org.koin.core.annotation.Single

@Single
class TestReviewRepo: ReviewRepo {
    override suspend fun saveReviewData(bookID: String, reviewData: Review, reviewID: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addReplyData(bookID: String, replyData: Reply, reviewID: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getReviewsData(bookID: String): List<Review>? {
        return listOf(
            Review(
                1,
                "nghixuana",
                5,
                "tuỵttt",
                3,
                0,
                listOf(),
            ),

        )
    }

    override suspend fun getRepliesData(bookID: String, reviewID: Int): List<Reply>? {
        return listOf(
            Reply(
                0,
                "linh",
                "yeahh",
                0
            )
        )
    }

    override suspend fun getReviewData(bookID: String, reviewID: Int): Review? {
        TODO("Not yet implemented")
    }
}