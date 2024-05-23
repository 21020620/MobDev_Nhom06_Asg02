package com.example.mobdev2.ui.screens.book


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobdev2.CachingResults
import com.example.mobdev2.R
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.ui.components.CustomButton
import com.example.mobdev2.ui.components.ProgressDots
import com.example.mobdev2.ui.components.book.BookDetailTopUI
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.BookDetailScreenDestination
import com.example.mobdev2.ui.screens.destinations.ChaptersDestination
import com.example.mobdev2.ui.screens.destinations.ReadBookScreenDestination
import com.example.mobdev2.ui.screens.destinations.ReaderReviewsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@BookNavGraph
@Composable
@Destination
fun BookDetailScreen(
    bookID: String,
    navController: NavController? = null,
    navigator: DestinationsNavigator,
    viewModel: BookDetailViewModel = koinViewModel()
) {

    val book = viewModel.book
    val similarBooks = viewModel.similarBooks
    var added by remember {
        mutableStateOf(CachingResults.currentUser.library.contains(bookID))
    }

    LaunchedEffect(key1 = bookID) {
        viewModel.getBookDetails(bookID)
        viewModel.getSimilarBooks(bookID)
        viewModel.loadHighlight(bookID)
    }
    Scaffold(
        topBar = {
            BookDetailTopBar(
                onBackClicked = { navController?.navigateUp() },
                onAdd2LibClicked = {
                    viewModel.addBookToLib(bookID)
                    added = true
                                   },
                bookAdded = added
            ) },
        bottomBar = { BookDetailBottomBar(
            onForumClicked = {
                    navigator.navigate(ReaderReviewsScreenDestination(bookID))
            },
            onChapterClicked = {
                book?.let {
                    navigator.navigate(ChaptersDestination(bookData = it))
                }
            },
            onStartReadingClicked = {
                navigator.navigate(ReadBookScreenDestination(bookID = bookID))
            }
        ) },

        content = { paddingValues ->

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressDots()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                    ) {
                        book?.let {
                            BookDetailTopUI(
                                title = it.title,
                                authors = it.author,
                                imageData = it.imageURL,
                            )

                            Text(
                                text = "Synopsis",
                                fontSize = 20.sp,
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 12.dp, end = 8.dp).testTag("synopsis"),
                            )

                            Text(
                                text = it.synopsis,
                                modifier = Modifier.padding(14.dp),
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Justify,
                                color = MaterialTheme.colorScheme.onBackground,
                            )

                            Spacer(modifier = Modifier.height(70.dp))

                            SimilarBooks(similarBooks) {bookID ->
                                navigator.navigate(BookDetailScreenDestination(bookID = bookID))
                            }
                        }
                    }
                }
            }

        })
}

@Composable
fun BookDetailTopBar(
    onBackClicked: () -> Unit,
    onAdd2LibClicked: () -> Unit,
    bookAdded: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .padding(start = 20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .clickable { onBackClicked() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(5.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Book Details",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 2.dp),
            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier
            .padding(end = 20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .clickable { onAdd2LibClicked() }) {
            Icon(
                imageVector = if(!bookAdded) Icons.Outlined.Add else Icons.Outlined.Check,
                contentDescription = "Add book to lib",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}


@Composable
fun BookDetailBottomBar(
    onForumClicked: () -> Unit, onChapterClicked: () -> Unit, onStartReadingClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .padding(start = 40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .clickable { onForumClicked() }) {
            Icon(
                painter = painterResource(id = R.drawable.forum),
                contentDescription = "Forum",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(5.dp)
                    .size(25.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))


        CustomButton(
            surfaceModifier = Modifier
                .width(150.dp)
                .height(45.dp),
            onClick = { onStartReadingClicked() },
            text = "Read Now",
            fontSize = 15.sp,
            shape = RoundedCornerShape(30)
        )


        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier
            .padding(end = 40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .clickable { onChapterClicked() }) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Content",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun SimilarBooks(similarBooks : List<Book>, onClick: (String) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.inverseOnSurface)

    ) {
        Text(
            text = "You might also like",
            modifier = Modifier.padding(18.dp),
            fontStyle = MaterialTheme.typography.displayLarge.fontStyle,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(similarBooks) { book ->
                Column(
                    Modifier
                        .padding(top = 70.dp)
                        .width(130.dp)
                        .clickable { onClick(book.id) }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(book.imageURL)
                            .crossfade(true).build(),
                        placeholder = painterResource(id = R.drawable.placeholder_cat),
                        contentDescription = "Book cover image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.title,
                        fontStyle = MaterialTheme.typography.displaySmall.fontStyle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = book.author,
                        fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}



