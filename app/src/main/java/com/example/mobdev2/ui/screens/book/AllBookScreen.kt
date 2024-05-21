package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobdev2.CachingResults
import com.example.mobdev2.R
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.BookDetailScreenDestination
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import com.example.mobdev2.ui.theme.figeronaFont
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@BookNavGraph(start = true)
@Destination
@Composable
fun AllBookScreen(
    navigator: DestinationsNavigator,
    viewModel: AllBookViewModel = koinViewModel()
) {

    val bookList = viewModel.bookList.collectAsState()
    val isSearching = viewModel.isSearching.collectAsState()
    val searchString = viewModel.searchString.collectAsState()
    val listType = viewModel.listType.collectAsState()

    if (bookList.value.isEmpty()) {
        LaunchedEffect(Unit) {
            viewModel.fetchBooks()
        }
        return Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    if (isSearching.value)
                        TextField(
                            value = searchString.value,
                            onValueChange = viewModel::updateSearchString,
                            modifier = Modifier
                                .padding(start = 5.dp, end = 20.dp)
                                .height(50.dp)
                                .fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 16.sp),
                            placeholder = {
                                Text("Search")
                            }
                        )
                    else
                        Column {
                            Text(
                                text = listType.value,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 2.dp),
                                fontSize = 22.sp,
                                fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            DeterminateIndicator()

                        }

                },
                actions = {
                    IconButton(onClick = viewModel::updateSearchState) {
                        Icon(
                            imageVector = if (isSearching.value) Icons.Default.Cancel else Icons.Default.Search,
                            contentDescription = "Search for books"
                        )
                    }
                    IconButton(onClick = viewModel::updateListType) {
                        Icon(
                            imageVector = if (listType.value == "All Books") Icons.Outlined.FavoriteBorder else Icons.Outlined.Favorite,
                            contentDescription = "Show recommendation books"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isSearching.value) {
                if (listType.value == "All Books") {
                    items(bookList.value.size) { idx ->
                        BookItemCard(
                            bookList.value[idx],
                            onClick = {
                                navigator.navigate(BookDetailScreenDestination(bookID = bookList.value[idx].id))
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                } else {
                    bookList.value.filter {
                        it.subjects.intersect(CachingResults.currentUser.favouriteTags.toSet())
                            .isNotEmpty()
                    }.map { it ->
                        item(
                            key = it.title
                        ) {
                            BookItemCard(
                                book = it
                            ) { bookID ->
                                navigator.navigate(BookDetailScreenDestination(bookID = bookID))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            } else {
                bookList.value
                    .filter {
                        it.title.contains(searchString.value, true)
                    }
                    .map { it ->
                        item(
                            key = it.title
                        ) {
                            BookItemCard(
                                it,
                                onClick = { bookID ->
                                    navigator.navigate(BookDetailScreenDestination(bookID = bookID))
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
            }
        }
    }
}

@Composable
fun DeterminateIndicator() {
    var currentProgress by remember { mutableStateOf(0f) }
    var timeLeftToReachGoal by remember { mutableStateOf(0L) }
    val db = FirebaseFirestore.getInstance()
    val email = FirebaseAuth.getInstance().currentUser?.email
    val username = email?.indexOf('@')?.let { email.substring(0, it) }
    val userDocumentRef = username?.let { db.collection("users").document(it) }
    if (userDocumentRef != null) {
        userDocumentRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val sessionDuration = document.getLong("session") ?: 0L
                val goal = document.getLong("goal") ?: 1L
                Log.d("GOAL", "$goal")
                Log.d("SESSION", "$sessionDuration")
                currentProgress = sessionDuration.toFloat() / goal.toFloat()
                timeLeftToReachGoal = goal - sessionDuration
                Log.d("PROGRESS", "$currentProgress")
                Log.d("TIME LEFT", "$timeLeftToReachGoal")
            }
        }
    }

    Row {
        CircularProgressIndicator(
            progress = currentProgress,
            modifier = Modifier
                .width(15.dp)
                .height(15.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Today's Reading",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 2.dp),
            style = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontStyle = MaterialTheme.typography.labelSmall.fontStyle
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$timeLeftToReachGoal minutes left",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 2.dp),
            style = TextStyle(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                fontStyle = MaterialTheme.typography.labelSmall.fontStyle
            )
        )
    }


}

@Composable
fun BookItemCard(
    book: Book,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth()
            .clickable { onClick(book.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            val imageBackground = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            }
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(imageBackground)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(book.imageURL)
                        .crossfade(true).build(),
                    placeholder = painterResource(id = R.drawable.placeholder_cat),
                    contentDescription = stringResource(id = R.string.cover_image_desc),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = book.title,
                    modifier = Modifier
                        .padding(
                            start = 12.dp, end = 8.dp
                        )
                        .fillMaxWidth(),
                    fontStyle = MaterialTheme.typography.displayLarge.fontStyle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = book.author,
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                    fontSize = 14.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.language,
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                )

                Text(
                    text = book.subjects.joinToString(", "),
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
