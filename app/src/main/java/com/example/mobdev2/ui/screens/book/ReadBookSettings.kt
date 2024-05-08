package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.theme.georgia
import com.example.mobdev2.ui.theme.sanSerif
import com.example.mobdev2.ui.theme.serif
import com.example.mobdev2.ui.theme.tertiaryContainerLight
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


enum class TextScaleButtonType { INCREASE, DECREASE }


@BookNavGraph
@Destination
@Composable
fun ReadBookSettings(
    navController: NavController? = null,
    viewModel: ReadBookViewModel = koinViewModel()
) {
    val fonts = listOf(
        "Times New Roman",
        "Georgia",
        "Inter",
        "SansSerif",
        "Serif",
        "OpenDyslexic",
        "System Default"
    )

    val selectedFont = remember { mutableStateOf(fonts[6]) }

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                        4.dp
                    ),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        stringResource(id = R.string.reader_setting),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 2.dp),
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Setting",
                        modifier = Modifier.padding(end = 10.dp)
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            SampleCard(selectedFont = selectedFont)
//            Text(
//                text = "Text size",
//                modifier = Modifier.padding(20.dp),
//                fontSize = 20.sp,
//
//                fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
//            )

//            TextScaleControls(
//                snackBarHostState = snackBarHostState
//            )

            Text(
                text = "Change Font",
                modifier = Modifier.padding(20.dp),
                fontSize = 20.sp,
                fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
            )
            FontChooser(selectedFont = selectedFont, fonts = fonts)
        }
    }
}

@Composable
private fun SampleCard(selectedFont: MutableState<String>) {
    var textSize by remember { mutableStateOf(14.sp) }
    var cardBackgroundColor by remember { mutableStateOf(Color.White) }
    var iconImage by remember { mutableStateOf(Icons.Outlined.LightMode) }
    var textColor by remember { mutableStateOf(Color.Black) }
    val openDyslexic = FontFamily(Font(resId = R.font.open_dyslexic_regular))
    val times = FontFamily(Font(resId = R.font.times))
    val fontMap = mapOf(
        "Times New Roman" to times,
        "Georgia" to georgia,
        "SansSerif" to sanSerif,
        "Serif" to serif,
        "OpenDyslexic" to openDyslexic,
        "System Default" to FontFamily.Default

    )
    Log.d("font", fontMap.toString())
    Log.d("selectedFont", selectedFont.value)
    val fontfam = fontMap[selectedFont.value] ?: FontFamily.Default
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardBackgroundColor,
            contentColor = MaterialTheme.colorScheme.onSurface,

            ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Aa",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(id = R.string.sample_text),
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Justify,
            fontSize = textSize,
            fontFamily = fontfam,
        )
    }
    Row(
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                iconImage = if (iconImage == Icons.Outlined.LightMode) {
                    Icons.Outlined.RemoveRedEye
                } else if (iconImage == Icons.Outlined.RemoveRedEye) {
                    Icons.Outlined.NightsStay
                } else {
                    Icons.Outlined.LightMode
                }

                val previousCardBackgroundColor = cardBackgroundColor

                cardBackgroundColor = if (previousCardBackgroundColor == Color.White) {
                    tertiaryContainerLight
                } else if (previousCardBackgroundColor == tertiaryContainerLight) {
                    Color.Black
                } else {
                    Color.White
                }

                textColor = if (cardBackgroundColor == Color.Black) {
                    Color.White
                } else {
                    Color.Black
                }
            }) {
            Icon(
                imageVector = iconImage,
                contentDescription = "light/dark mode"
            )
        }

        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {

            }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_reader_text_minus),
                contentDescription = "smaller font size"
            )
        }

        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {

            }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_reader_text_plus),
                contentDescription = "bigger font size"
            )
        }
    }
}


//@Composable
//private fun TextScaleControls(
////    viewModel: ReaderViewModel,
//    snackBarHostState: SnackbarHostState
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        ReaderTextScaleButton(
//            buttonType = TextScaleButtonType.DECREASE,
////            fontSize = viewModel.state.fontSize,
//            fontSize = 18,
//            snackBarHostState = snackBarHostState,
//            onFontSizeChanged = {}
//        )
//
//        Spacer(modifier = Modifier.width(14.dp))
//
//        Box(
//            modifier = Modifier
//                .size(100.dp, 45.dp)
//                .clip(RoundedCornerShape(16.dp))
//                .background(ButtonDefaults.filledTonalButtonColors().containerColor),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "100",
//
//                fontSize = 20.sp,
//                color = MaterialTheme.colorScheme.onSurface,
//                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.width(14.dp))
//
//        ReaderTextScaleButton(
//            buttonType = TextScaleButtonType.INCREASE,
////            fontSize = viewModel.state.fontSize,
//            fontSize = 18,
//            snackBarHostState = snackBarHostState,
//            onFontSizeChanged = {}
//        )
//    }
//}
//
//@Composable
//private fun ReaderTextScaleButton(
//    buttonType: TextScaleButtonType,
//    fontSize: Int,
//    snackBarHostState: SnackbarHostState,
//    onFontSizeChanged: (newValue: Int) -> Unit
//) {
//    val coroutineScope = rememberCoroutineScope()
//
//    val context = LocalContext.current
//    val (iconRes, adjustment) = remember(buttonType) {
//        when (buttonType) {
//            TextScaleButtonType.DECREASE -> Pair(R.drawable.ic_reader_text_minus, -10)
//            TextScaleButtonType.INCREASE -> Pair(R.drawable.ic_reader_text_plus, 10)
//        }
//    }
//
//    val callback: () -> Unit = {
//        val newSize = fontSize + adjustment
//        when {
//            newSize < 50 -> {
//                coroutineScope.launch {
//                    snackBarHostState.showSnackbar(
//                        context.getString(R.string.reader_min_font_size_reached),
//                        null
//                    )
//                }
//            }
//
//            newSize > 200 -> {
//                coroutineScope.launch {
//                    snackBarHostState.showSnackbar(
//                        context.getString(R.string.reader_max_font_size_reached),
//                        null
//                    )
//                }
//            }
//
//            else -> {
//                coroutineScope.launch {
//                    val adjustedSize = fontSize + adjustment
//                    onFontSizeChanged(adjustedSize)
//                }
//            }
//        }
//    }
//
//    FilledTonalButton(
//        onClick = { callback() },
//        modifier = Modifier.size(80.dp, 45.dp),
//        shape = RoundedCornerShape(18.dp),
//    ) {
//        Icon(
//            imageVector = ImageVector.vectorResource(id = iconRes),
//            contentDescription = null,
//            modifier = Modifier.size(22.dp)
//        )
//    }
//}


@Composable
private fun FontChooser(
    selectedFont : MutableState<String>,
    fonts: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentSize().padding(10.dp)) {
        Text(selectedFont.value, modifier = Modifier.clickable(onClick = { expanded = true }))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            fonts.forEach { font ->
                DropdownMenuItem(
                    text = { Text(font) },
                    onClick = {
                        selectedFont.value = font
                        expanded = false
                    })
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReadBookSettingsPV() {
    ReadBookSettings()
}

