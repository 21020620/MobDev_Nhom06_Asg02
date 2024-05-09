package com.example.mobdev2.ui.screens.book


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.CustomTopAppBar
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.theme.errorDark
import com.example.mobdev2.ui.theme.primaryDark
import com.example.mobdev2.ui.theme.secondaryDark
import com.example.mobdev2.ui.theme.tertiaryDark
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

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CustomTopAppBar(headerText = "Display Settings") {
                navController?.navigateUp()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {
            Text(
                text = "Background Color",
                modifier = Modifier.padding(20.dp),
                fontSize = 20.sp,

                fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
            )
            Row(
                modifier = Modifier
                    .padding(start = 100.dp)
                    .height(IntrinsicSize.Min)
            ) {
                val colors = listOf(primaryDark, secondaryDark, tertiaryDark, errorDark)
                val borderColor = Color.Gray

                Spacer(modifier = Modifier.padding(6.dp))

                colors.forEach { color ->
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(color),
                        modifier = Modifier
                            .padding(1.dp)
                            .size(40.dp)
                            .border(BorderStroke(2.dp, borderColor)),
                        shape = RectangleShape
                    ) {}

                    Spacer(modifier = Modifier.padding(6.dp))
                }
            }
        }

        Text(
            text = "Text size",
            modifier = Modifier.padding(20.dp),
            fontSize = 20.sp,

            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
        )

        TextScaleControls(
//                viewModel = viewModel,
            snackBarHostState = snackBarHostState
        )

        Text(
            text = "Change FontStyle",
            modifier = Modifier.padding(20.dp),
            fontSize = 20.sp,

            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
        )

        FontChooser()

    }
}

@Composable
private fun TextScaleControls(
//    viewModel: ReaderViewModel,
    snackBarHostState: SnackbarHostState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        ReaderTextScaleButton(
            buttonType = TextScaleButtonType.DECREASE,
//            fontSize = viewModel.state.fontSize,
            fontSize = 18,
            snackBarHostState = snackBarHostState,
            onFontSizeChanged = {}
        )

        Spacer(modifier = Modifier.width(14.dp))

        Box(
            modifier = Modifier
                .size(100.dp, 45.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ButtonDefaults.filledTonalButtonColors().containerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "100",

                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        ReaderTextScaleButton(
            buttonType = TextScaleButtonType.INCREASE,
//            fontSize = viewModel.state.fontSize,
            fontSize = 18,
            snackBarHostState = snackBarHostState,
            onFontSizeChanged = {}
        )
    }
}

@Composable
private fun ReaderTextScaleButton(
    buttonType: TextScaleButtonType,
    fontSize: Int,
    snackBarHostState: SnackbarHostState,
    onFontSizeChanged: (newValue: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val (iconRes, adjustment) = remember(buttonType) {
        when (buttonType) {
            TextScaleButtonType.DECREASE -> Pair(R.drawable.ic_reader_text_minus, -10)
            TextScaleButtonType.INCREASE -> Pair(R.drawable.ic_reader_text_plus, 10)
        }
    }

    val callback: () -> Unit = {
        val newSize = fontSize + adjustment
        when {
            newSize < 50 -> {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        context.getString(R.string.reader_min_font_size_reached),
                        null
                    )
                }
            }

            newSize > 200 -> {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        context.getString(R.string.reader_max_font_size_reached),
                        null
                    )
                }
            }

            else -> {
                coroutineScope.launch {
                    val adjustedSize = fontSize + adjustment
                    onFontSizeChanged(adjustedSize)
                }
            }
        }
    }

    FilledTonalButton(
        onClick = { callback() },
        modifier = Modifier.size(80.dp, 45.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
    }
}


@Composable
private fun FontChooser(
) {
    val fonts = listOf(
        "Cursive",
        "Figerona",
        "Inter",
        "OpenDyslexic",
        "SansSerif",
        "Serif",
        "System Default"
    )
    val radioOptions = fonts.map { it }
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(radioOptions.first().toString())
    }

    Column(
        modifier = Modifier
            .selectableGroup()
            .padding(start = 30.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        radioOptions.forEach { text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected },
                        role = Role.RadioButton,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.inversePrimary,
                        disabledSelectedColor = Color.Black,
                        disabledUnselectedColor = Color.Black
                    ),
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReadBookSettingsPV() {
    ReadBookSettings()
}

