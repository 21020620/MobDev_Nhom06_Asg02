package com.example.mobdev2.ui.screens.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.ui.components.CustomTopAppBar
import com.example.mobdev2.ui.components.book.BookDetailTopUI
import com.example.mobdev2.ui.components.simpleVerticalScrollbar
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.ReadBookScreenDestination
import com.example.mobdev2.ui.theme.figeronaFont
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@BookNavGraph
@Destination
@Composable
fun Chapters (
    viewModel: ReadBookViewModel = koinViewModel(),
    navController: NavController? = null,
    navigator: DestinationsNavigator,
    bookData: Book
) {

    LaunchedEffect(key1 = true) {
        viewModel.loadReaderData("Z7sXjKwP6XL46c2CNW54")
    }
    val readerItem = viewModel.chaptersState.readerData


    Scaffold(
        topBar = {
            CustomTopAppBar(headerText = "Ebook Reader") {
                navController?.navigateUp()
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = if (readerItem != null) "Resume" else "Start") },
                onClick = {
                          navigator.navigate(ReadBookScreenDestination(bookID = "Z7sXjKwP6XL46c2CNW54"))
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_reader_fab_button),
                        contentDescription = null
                    )
                },
                modifier = Modifier.padding(end = 10.dp, bottom = 8.dp),
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {

            BookDetailTopUI(
                title = bookData.title,
                authors = bookData.author,
                imageData = bookData.imageURL,
                progressPercent = readerItem?.getProgressPercent(bookData.chapters.size)
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    start = 20.dp, end = 20.dp, top = 2.dp, bottom = 2.dp
                ),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            )

            val lazyListState = rememberLazyListState()
            LazyColumn(
                state = lazyListState, modifier = Modifier.simpleVerticalScrollbar(
                    lazyListState, color = MaterialTheme.colorScheme.primary
                )
            ) {
                items(bookData.chapters.size) { idx ->
                    ChapterItem(chapterTitle = bookData.chapters[idx].name, onClick = {
                        viewModel.updateReaderProgress("Z7sXjKwP6XL46c2CNW54", idx, 0 )
                        navigator.navigate(ReadBookScreenDestination(bookID = "Z7sXjKwP6XL46c2CNW54"))
                    })
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ChapterItem(chapterTitle: String, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                2.dp
            )
        ),
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 12.dp),
                text = chapterTitle,
                fontFamily = figeronaFont,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Icon(
                modifier = Modifier
                    .size(15.dp)
                    .weight(0.4f),
                painter = painterResource(id = R.drawable.ic_right_arrow),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

}
