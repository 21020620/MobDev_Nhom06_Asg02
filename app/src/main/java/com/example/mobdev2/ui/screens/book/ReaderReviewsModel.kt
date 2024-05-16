package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.repo.ReviewRepository
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.Reply
import com.example.mobdev2.repo.model.Review
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ReaderReviewsModel(
    private val reviewRepo: ReviewRepository,
) : ViewModel() {

    var reviews by mutableStateOf(listOf<Review>())

    private val email = FirebaseAuth.getInstance().currentUser?.email
    private val userID = email?.indexOf('@')?.let { email.substring(0, it) }


    private val _rating = mutableDoubleStateOf(0.0)
    val rating: State<Double> = _rating

    var isLoading by mutableStateOf<Boolean>(true)


    fun loadReviews(bookID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reviews = reviewRepo.getReviewsData(bookID)!!
                reviews = reviews.map { review ->
                    val replies = reviewRepo.getRepliesData(bookID, review.reviewID) ?: listOf()
                    review.copy(replies = replies)
                }
                updateRating()
                isLoading = false
            } catch (e: Exception) {
                Log.e("FETCH DATA FAILURE", "$e")
            }
        }
    }

    fun loadReview(bookID: String, reviewID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedReview = reviewRepo.getReviewData(bookID, reviewID)
                val updatedReplies = reviewRepo.getRepliesData(bookID, reviewID) ?: listOf()
                val newReview = updatedReview?.copy(replies = updatedReplies)
                reviews = reviews.map { review ->
                    if (review.reviewID == reviewID) newReview ?: review else review
                }
                updateRating()
            } catch (e: Exception) {
                Log.e("FETCH DATA FAILURE", "$e")
            }
        }
    }

    fun replyReview(bookID: String, reviewID: Int, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (userID != null) {
                val reply = Reply(reviewID + 100, userID, content, 0)
                reviewRepo.addReplyData(bookID, reply, reviewID)
                loadReview(bookID, reviewID)
            }
        }
    }

    fun reviewBook(bookID: String, content: String, rating: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if(userID != null) {
                val review = Review(reviews.size, userID, rating, content, 0, 0, listOf())
                reviewRepo.saveReviewData(bookID, review, reviews.size)
                loadReviews(bookID)
                updateRating()
            }
        }
    }

    private fun updateRating() {
        if (reviews.isEmpty()) {
            _rating.doubleValue = 0.0
        }
        val totalRating = reviews.sumOf { it.rating }
        _rating.doubleValue =  totalRating.toDouble() / reviews.size
    }

    fun updateLikesCount(bookID: String, likesCount: Int, reviewID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            reviewRepo.updateReviewLikes(bookID, reviewID, likesCount)
            val updatedReviews = reviews.map { review ->
                if (review.reviewID == reviewID) {
                    review.copy(likesCount = likesCount)
                } else {
                    review
                }
            }
            reviews = updatedReviews
        }
    }
}