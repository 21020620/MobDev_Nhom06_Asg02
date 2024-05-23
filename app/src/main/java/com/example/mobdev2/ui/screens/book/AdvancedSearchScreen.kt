package com.example.mobdev2.ui.screens.book

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobdev2.CachingResults
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.BookDetailScreenDestination
import com.example.mobdev2.ui.screens.destinations.ReadBookScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@BookNavGraph
@Destination
@Composable
fun AdvancedSearchScreen (
    viewModel: AdvancedSearchViewModel = koinViewModel(),
    navigator: DestinationsNavigator
) {
    val searchContent = viewModel.searchContent.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Advanced Search",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 2.dp),
                        fontSize = 22.sp,
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
            ) {
                TextField(
                    value = searchContent.value,
                    onValueChange = viewModel::setSearchContent,
                    modifier = Modifier
                        .testTag("input")
                        .weight(4f)
                        .padding(4.dp, 2.dp),
                )
                Button(
                    modifier = Modifier
                        .padding(0.dp, 4.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .weight(1f),
                    onClick = viewModel::search
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                        contentDescription = "SearchButton"
                    )
                }
            }
            LazyColumn {
                CachingResults.bookList
                    .filter { searchResults.value.containsKey(it.id) }
                    .map { book ->
                        item(key = book.id) {
                            Spacer(modifier = Modifier.height(3.dp))
                            BookItemCard(
                                book,
                                onClick = { bookID ->
                                    navigator.navigate(BookDetailScreenDestination(bookID = bookID))
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            searchResults.value[book.id]?.map {
                                ChapterItem(chapterTitle = "Chapter ${it+1}", onClick = {
                                    viewModel.updateReaderProgress(book.title, it, 0)
                                    navigator.navigate(ReadBookScreenDestination(bookID = book.title))
                                })
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.scrim
                            )
                        }
                    }
            }
        }
    }
}