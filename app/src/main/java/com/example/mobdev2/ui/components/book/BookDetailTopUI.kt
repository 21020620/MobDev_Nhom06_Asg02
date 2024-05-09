package com.example.mobdev2.ui.components.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobdev2.R


@Composable
fun BookDetailTopUI(
    title: String,
    authors: String,
    imageData: Any?,
    progressPercent: String? = null
) {
    println("progress percent: $progressPercent")
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
    ) {
        AsyncImage(
            model = R.drawable.book_details_bg,
            contentDescription = null,
            alpha = 0.35f,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        ), startY = 8f
                    )
                )
        )

        Row(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val imageBackground = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)

                Box(
                    modifier = Modifier
                        .shadow(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(imageBackground)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageData)
                            .crossfade(true).build(),
                        placeholder = painterResource(id = R.drawable.placeholder_cat),
                        contentDescription = null,
                        modifier = Modifier
                            .width(118.dp)
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(
                            start = 12.dp, end = 12.dp, top = 20.dp
                        )
                        .fillMaxWidth(),
                    fontSize = 24.sp,
                    
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = authors,
                    modifier = Modifier.padding(
                        start = 12.dp, end = 8.dp, top = 4.dp
                    ),
                    fontSize = 18.sp,
                    
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                progressPercent?.let {
                    Text(
                        text = "$progressPercent% Completed",
                        modifier = Modifier.padding(
                            start = 12.dp, end = 8.dp, top = 8.dp
                        ),
                        fontSize = 16.sp,
                        
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    BookDetailTopUI(
        title = "The Great Gatsby",
        authors = "F. Scott Fitzgerald",
        imageData = null,
    )

}