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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.CustomButton
import com.example.mobdev2.ui.components.book.BookDetailTopUI
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.theme.figeronaFont
import com.example.mobdev2.ui.theme.pacificoFont
import com.ramcosta.composedestinations.annotation.Destination

@BookNavGraph
@Composable
@Destination
fun BookDetailScreen(
//    bookId: String,
    navController: NavController? = null
) {

    Scaffold(
        topBar = { BookDetailTopBar(onBackClicked = {
            navController?.navigateUp()
        }, onAdd2LibClicked = { }) },
        bottomBar = { BookDetailBottomBar(onBackClicked = { }, onShareClicked = { }) },
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
                    val authors = "Dang"

                    BookDetailTopUI(
                        title = "Book Title",
                        authors = authors,
                        imageData = R.drawable.placeholder_cat,
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
                        text = stringResource(id = R.string.book_synopsis),
                        modifier = Modifier.padding(14.dp),
                        fontFamily = figeronaFont,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        })
}

@Composable
fun BookDetailTopBar(
    onBackClicked: () -> Unit, onAdd2LibClicked: () -> Unit
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
            fontFamily = figeronaFont,
            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier
            .padding(end = 20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .clickable { onAdd2LibClicked() }) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add book to lib",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}


@Composable
fun BookDetailBottomBar(
    onBackClicked: () -> Unit, onShareClicked: () -> Unit
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
            .clickable { onBackClicked() }) {
            Icon(
                painter = painterResource(id = R.drawable.forum),
                contentDescription = stringResource(id = R.string.back_button_desc),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(5.dp).size(25.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))


        CustomButton(
            surfaceModifier = Modifier
                .width(150.dp)
                .height(45.dp),
            onClick = {},
            text = "Start Reading",
            fontSize = 15.sp,
            shape = RoundedCornerShape(30)
        )


        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier
            .padding(end = 40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .clickable { onShareClicked() }) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Content",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}



