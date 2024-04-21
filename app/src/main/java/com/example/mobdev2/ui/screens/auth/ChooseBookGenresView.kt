package com.example.mobdev2.ui.screens.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.CustomButton
import com.example.mobdev2.ui.components.CustomImageButton
import com.example.mobdev2.ui.components.auth.EmailField
import com.example.mobdev2.ui.components.auth.LoginButtonGroup
import com.example.mobdev2.ui.components.auth.PasswordField
import com.example.mobdev2.ui.components.auth.PromptRow
import com.example.mobdev2.ui.components.auth.SignUpButtonGroup
import com.example.mobdev2.ui.components.auth.Title
import com.example.mobdev2.ui.navigation.Screens
import com.example.mobdev2.ui.theme.MobDev2Theme
import com.example.mobdev2.ui.theme.spacing

@Composable
fun ChooseBookGenresView(navController: NavController? = null) {


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

                item {
                    CustomImageButton(
                        imageID = R.drawable.art_books,
                        content = "Art Books",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                item {
                    CustomImageButton(
                        imageID = R.drawable.history_books,
                        content = "History Books",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }


                item {
                    CustomImageButton(
                        imageID = R.drawable.biographies,
                        content = "Biographies",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                item {
                    CustomImageButton(
                        imageID = R.drawable.kids_books,
                        content = "Kids Books",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                item {
                    CustomImageButton(
                        imageID = R.drawable.romance,
                        content = "Romance",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                item {
                    CustomImageButton(
                        imageID = R.drawable.medical_books,
                        content = "Medical Books",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                item {
                    CustomImageButton(
                        imageID = R.drawable.mystery,
                        content = "Mystery",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }


                item {
                    CustomImageButton(
                        imageID = R.drawable.science_fiction,
                        content = "Science Fiction",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                item {
                    CustomImageButton(
                        imageID = R.drawable.science_books,
                        content = "Science Books",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                item {
                    CustomImageButton(
                        imageID = R.drawable.fantasy,
                        content = "Fantasy",
                        backgroundColor = 0xF8FFC673,
                        onClick = { },
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
                    onClick = {},
                    text = "Continue",
                    fontSize = 20.sp,
                    shape = RoundedCornerShape(24)
                )

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriesPreview() {
    MobDev2Theme { ChooseBookGenresView() }
}