package com.example.mobdev2.ui.screens.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.CustomButton
import com.example.mobdev2.ui.components.CustomImageButton
import com.example.mobdev2.ui.components.auth.Title
import com.example.mobdev2.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@RootNavGraph(start=true)
@Destination
@Composable
fun PickBookGenresScreen(
    navigator: DestinationsNavigator,
    viewModel: PickBookGenresViewModel = koinViewModel(parameters = {
        parametersOf(navigator)
    })
) {

    val bookGenreList: ArrayList<BookGenre> = arrayListOf(
        BookGenre(R.drawable.art_books, "Art Books"),
        BookGenre(R.drawable.history_books, "History Books"),
        BookGenre(R.drawable.biographies, "Biographies"),
        BookGenre(R.drawable.kids_books, "Kids Books"),
        BookGenre(R.drawable.romance, "Romance"),
        BookGenre(R.drawable.medical_books, "Medical Books"),
        BookGenre(R.drawable.mystery, "Mystery"),
        BookGenre(R.drawable.science_fiction, "Science Fiction"),
        BookGenre(R.drawable.science_books, "Science Books"),
        BookGenre(R.drawable.fantasy, "Fantasy"),
    )

    val pickedList = viewModel.pickedList.collectAsState()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize()
                    .weight(0.5f),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    modifier = Modifier.requiredSize(180.dp),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "LOGO"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
            ) {
                Title(
                    title = "Select your interests",
                    subtitle = "Please select two or more to proceed."
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(start = 40.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {

                items(bookGenreList.size) { index ->
                    CustomImageButton(
                        imageID = bookGenreList[index].imageID,
                        content = bookGenreList[index].name,
                        backgroundColor = 0xF8FFC673,
                        isClicked = pickedList.value[index],
                        onClick = {
                            viewModel.setPickedList(index)
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(top = 50.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    onClick = viewModel::navigateToHome,
                    text = "Continue",
                    fontSize = 20.sp,
                    shape = RoundedCornerShape(24)
                )
            }
        }
    }
}

data class BookGenre(
    val imageID: Int,
    val name: String
)

