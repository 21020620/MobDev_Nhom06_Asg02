package com.example.mobdev2.ui.screens.book

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobdev2.R
import com.example.mobdev2.model.Book
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.BookDetailScreenDestination
import com.example.mobdev2.ui.theme.figeronaFont
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

    if(bookList.value.isEmpty()) {
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
                title = {
                    Text(text = "All Books")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                           imageVector = Icons.Default.Favorite,
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
            items(bookList.value.size) { idx ->
                BookItemCard(
                    bookList.value[idx],
                    onClick = {
                        navigator.navigate(BookDetailScreenDestination)
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}
@Composable
fun BookItemCard(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth(),
        onClick = onClick,
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
                    fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                    fontSize = 18.sp,
                    fontFamily = figeronaFont,
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
                    fontFamily = figeronaFont,
                    fontSize = 14.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.language,
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                    fontFamily = figeronaFont
                )

                Text(
                    text = book.subjects.joinToString(", "),
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp, bottom = 2.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                    fontFamily = figeronaFont,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
