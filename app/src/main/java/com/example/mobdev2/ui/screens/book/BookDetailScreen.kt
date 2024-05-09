package com.example.mobdev2.ui.screens.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.CachingResults
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.CustomButton
import com.example.mobdev2.ui.components.book.BookDetailTopUI
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.ChaptersDestination
import com.example.mobdev2.ui.screens.destinations.ReadBookScreenDestination
import com.example.mobdev2.ui.theme.figeronaFont
import com.example.mobdev2.ui.theme.pacificoFont
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
    var added by remember {
        mutableStateOf(CachingResults.currentUser.library.contains(bookID))
    }

    LaunchedEffect(key1 = bookID) {
        viewModel.getBookDetails(bookID)
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
            onForumClicked = { },
            onChapterClicked = {
                book?.let {
                    navigator.navigate(ChaptersDestination(bookData = it))
                }
            },
            onStartReadingClicked = {
                navigator.navigate(ReadBookScreenDestination(bookID = "Z7sXjKwP6XL46c2CNW54"))
            }
        ) },

        content = { paddingValues ->

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
                            fontFamily = figeronaFont,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                        )

                        Text(
                            text = it.synopsis,
                            modifier = Modifier.padding(14.dp),
                            fontFamily = figeronaFont,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
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
            modifier = Modifier.padding(bottom = 2.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontFamily = pacificoFont,
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



