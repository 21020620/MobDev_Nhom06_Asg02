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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.theme.tertiaryContainerLight
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@BookNavGraph
@Destination
@Composable
fun ReadBookSettings(
    navController: NavController? = null,
    viewModel: ReadBookViewModel = koinViewModel(),

    ) {
    val fonts = listOf(
        "Times New Roman",
        "Georgia",
        "Inter",
        "SansSerif",
        "OpenDyslexic",
        "System Default"
    )

    val currentBackground = MaterialTheme.colorScheme.background
    val currentTextColor = MaterialTheme.colorScheme.onBackground

    val settingDataStore = UserPreferences(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()

    val textSize = remember { mutableStateOf(14f) }
    LaunchedEffect(Unit) {
        textSize.value = settingDataStore.fontSizeFlow.first()
    }
    val backgroundColorStr by settingDataStore.backgroundFlow.collectAsState(initial = "")
    val textColorStr by settingDataStore.textColorFlow.collectAsState(initial = "")
    val fontStr by settingDataStore.fontFlow.collectAsState(initial = "")

    val textColor = remember { mutableStateOf(currentTextColor) }
    textColor.value = if (textColorStr == "light") {
        lightColorScheme().onBackground
    } else {
        darkColorScheme().onBackground
    }

    val backgroundColor = remember { mutableStateOf(currentBackground) }
    backgroundColor.value = when(backgroundColorStr) {
        "light" -> lightColorScheme().background
        "dark" -> darkColorScheme().background
        "tertiary" -> tertiaryContainerLight
        else -> currentBackground
    }
    Log.d("backgroundColorStr", "$backgroundColor")



    val selectedFont = remember { mutableStateOf(fontStr) }
    selectedFont.value = fontStr
    Log.d("fontStr", "$selectedFont")

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
            SampleCard(
                selectedFont = selectedFont,
                textSize = textSize,
                textColor = textColor,
                cardBackgroundColor = backgroundColor)
            FontChooser(selectedFont = selectedFont, fonts = fonts)

        }
    }


}

@Composable
fun SampleCard(
    selectedFont: MutableState<String>,
    textSize: MutableState<Float>,
    textColor: MutableState<Color>,
    cardBackgroundColor: MutableState<Color>,
) {
    var iconImage by remember { mutableStateOf(Icons.Outlined.LightMode) }
    val increaseFontSize: () -> Unit = { textSize.value = (textSize.value + 2).coerceAtMost(32f) }
    val decreaseFontSize: () -> Unit = { textSize.value = (textSize.value - 2).coerceAtLeast(8f) }

    val fontMap = mapOf(
        "Times New Roman" to ReaderFont.Times,
        "Georgia" to ReaderFont.Georgia,
        "SansSerif" to ReaderFont.Sans,
        "OpenDyslexic" to ReaderFont.OpenDyslexic,
        "System Default" to ReaderFont.System

    )

    val selectedReaderFont = fontMap[selectedFont.value] ?: ReaderFont.System
    val fontfam = selectedReaderFont.fontFamily
    val lightBackground = remember { lightColorScheme().background }
    val darkBackground = remember { darkColorScheme().background }
    val lightText = remember { lightColorScheme().onBackground }
    val darkText = remember { darkColorScheme().onBackground }
    lateinit var settingDataStore: UserPreferences
    val localContext = LocalContext.current
    settingDataStore = UserPreferences(localContext)
    val courotineScope = rememberCoroutineScope()

    Log.d("cardBackgroundColor Card", cardBackgroundColor.value.toString())
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.elevatedCardColors(

            containerColor = cardBackgroundColor.value,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Text(
            text = "Aa",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = textColor.value
        )
        Text(
            text = stringResource(id = R.string.sample_text),
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Justify,
            style = TextStyle(
                fontSize = textSize.value.sp,
                fontFamily = fontfam,
                color = textColor.value
            )
        )
    }
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
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

                val previousCardBackgroundColor = cardBackgroundColor.value

                cardBackgroundColor.value = when (previousCardBackgroundColor) {
                    lightBackground -> tertiaryContainerLight
                    tertiaryContainerLight -> darkBackground
                    else -> lightBackground
                }
                Log.d("cardBackgroundColor", cardBackgroundColor.value.toString())

                textColor.value = if (cardBackgroundColor.value == darkBackground) {
                    darkText
                } else {
                    lightText
                }
                Log.d("textColor", textColor.value.toString())
                courotineScope.launch {
                    if (cardBackgroundColor.value == lightBackground) {
                        settingDataStore.saveBackground("light")
                    } else if (cardBackgroundColor.value == darkBackground) {
                        settingDataStore.saveBackground("dark")
                    } else {
                        settingDataStore.saveBackground("tertiary")
                    }
                    if (textColor.value == lightText) {
                        settingDataStore.saveTextColor("light")
                    } else {
                        settingDataStore.saveTextColor("dark")
                    }
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
                decreaseFontSize()
            courotineScope.launch {
                settingDataStore.saveFontSize(textSize.value)
            }
            }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_reader_text_minus),
                contentDescription = "smaller font size"
            )
        }

        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                increaseFontSize()
                courotineScope.launch {
                    settingDataStore.saveFontSize(textSize.value)
                }
            }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_reader_text_plus),
                contentDescription = "bigger font size"
            )
        }

    }
}


@Composable
private fun FontChooser(
    selectedFont: MutableState<String>,
    fonts: List<String>,
) {
    lateinit var settingDataStore: UserPreferences
    val localContext = LocalContext.current
    settingDataStore = UserPreferences(localContext)
    val courotineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = "Change font",
        modifier = Modifier.padding(10.dp),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight(900)),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
    ) {
        Text(
            selectedFont.value,
            modifier = Modifier
                .clickable(onClick = { expanded = true })
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp),
                ),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight(400)),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            fonts.forEach { font ->
                DropdownMenuItem(
                    text = { Text(font) },
                    onClick = {
                        selectedFont.value = font
                        expanded = false
                        courotineScope.launch {
                            settingDataStore.saveFont(font)
                        }
                    })
            }
        }
    }
}

