package com.example.mobdev2.ui.screens.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.mobdev2.repo.model.Reply
import com.example.mobdev2.repo.model.Review
import com.example.mobdev2.ui.components.CustomTopAppBar
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@BookNavGraph
@Destination
@Composable
fun ReaderReviewsScreen(
    bookID: String,
    navController: NavController? = null,
    viewModel: ReaderReviewsModel = koinViewModel()
) {

    var showDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var starRating by remember { mutableIntStateOf(5) }

    LaunchedEffect(bookID) {
        viewModel.loadReviews(bookID)
    }

    val rating by viewModel.rating

    Scaffold (
        topBar = {
            CustomTopAppBar(headerText = "Reader Reviews") {
                navController?.navigateUp()
            }
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Review") },
                onClick = {
                    showDialog = true
                },
                icon = {
                    Icon(Icons.Filled.Edit, contentDescription = "Write review")
                },
                modifier = Modifier.padding(end = 10.dp, bottom = 8.dp),
            )
        },
        content = {paddingValues ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {

                Text(
                    text = String.format("%.1f", rating),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < rating.roundToInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Rating Star $index",
                            tint = if (index < rating) MaterialTheme.colorScheme.secondary else Color.Gray
                        )
                    }
                }

                Text(
                    text = "${viewModel.reviews.size} reviews",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

//                HorizontalDivider(
//                    thickness = 2.dp,
//                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
//                )

                LazyColumn {
                    items(viewModel.reviews) { review ->
                        ReviewItem(viewModel, review, bookID)
                    }
                }
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Write a Review") },
                    text = {
                        Column {
                            StarRating(starRating = starRating) { rating ->
                                starRating = rating
                            }

                            BasicTextField(
                                value = reviewText,
                                onValueChange = { reviewText = it },
                                modifier = Modifier.padding(top = 16.dp),
                                decorationBox = { innerTextField ->
                                    if (reviewText.isEmpty()) {
                                        Text("Type your review here...", color = Color.Gray, fontSize = 14.sp)
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                viewModel.reviewBook(bookID, reviewText, starRating)
                                reviewText = ""
                                starRating = 5
                          }
                        ) {
                            Text("Submit")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    )

}

@Composable
fun ReviewItem(viewModel: ReaderReviewsModel, review: Review, bookID: String, modifier: Modifier = Modifier) {
    var showAllReplies by remember { mutableStateOf(false) }
    var showReplyDialog by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(start = 10.dp, top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = rememberImagePainter(data = "https://cellphones.com.vn/sforum/wp-content/uploads/2024/02/avatar-anh-meo-cute-54.jpg"),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column {
                Text(
                    text = review.readerName,
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = MaterialTheme.colorScheme.secondary
                            )
                    }
                }
                Text(
                    text = review.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row {
                    TextButton(onClick = { viewModel.updateLikesCount(bookID, review.likesCount + 1, review.reviewID, false) }) {
                        Text("Like (${review.likesCount})")
                    }
                    TextButton(onClick = { showReplyDialog = true }) {
                        Text("Reply (${review.repliesCount})")
                    }
                }

                // Display a limited number of replies or all based on state
                if (review.replies.isNotEmpty()) {
                    if (showAllReplies) {
                        review.replies.forEach { reply ->
                            ReplyItem(viewModel, bookID, reply, modifier = Modifier.padding(start = 10.dp))
                        }
                    } else {
                        review.replies.take(2).forEach { reply ->
                            ReplyItem(viewModel, bookID, reply, modifier = Modifier.padding(start = 10.dp))
                        }
                        if (review.replies.size > 2) {
                            TextButton(onClick = { showAllReplies = true }) {
                                Text("...See more")
                            }
                        }
                    }
                }

                if (showReplyDialog) {
                    AlertDialog(
                        onDismissRequest = { showReplyDialog = false },
                        title = { Text("Write a Reply") },
                        text = {
                            Column {
                                BasicTextField(
                                    value = replyText,
                                    onValueChange = { replyText = it },
                                    modifier = Modifier.padding(top = 16.dp),
                                    decorationBox = { innerTextField ->
                                        if (replyText.isEmpty()) {
                                            Text("Type your reply here...", color = Color.Gray, fontSize = 14.sp)
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.replyReview(bookID, review.reviewID, replyText)
                                    print(review.replies.size)
                                    replyText = ""
                                    showReplyDialog = false
                                }
                            ) {
                                Text("Submit")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showReplyDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReplyItem(viewModel: ReaderReviewsModel, bookID: String, reply: Reply, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 10.dp, top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = rememberImagePainter(data = "https://cellphones.com.vn/sforum/wp-content/uploads/2024/02/avatar-anh-meo-cute-54.jpg"),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = reply.readerName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = reply.content,
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { viewModel.updateLikesCount(bookID, reply.likesCount + 1, reply.replyID, true) }) {
                    Text("Like (${reply.likesCount})")
                }
            }
        }
    }
}

@Composable
fun StarRating(starRating: Int, onRatingChange: (Int) -> Unit) {
    Row {
        (1..5).forEach { index ->
            IconButton(onClick = { onRatingChange(index) }) {
                Icon(
                    imageVector = if (index <= starRating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Rating $index",
                    tint = if (index <= starRating) MaterialTheme.colorScheme.secondary else Color.Gray
                )
            }
        }
    }
}
