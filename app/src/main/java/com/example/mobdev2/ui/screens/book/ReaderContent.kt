package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdev2.repo.model.Chapter
import com.example.mobdev2.ui.theme.tertiaryContainerLight
import com.google.firebase.annotations.concurrent.Background
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first


private fun chunkText(text: String): List<String> {
    return text.splitToSequence("\n\n")
        .filter { it.isNotBlank() }
        .toList()
}

@Composable
fun ReaderContent(
    viewModel: ReadBookViewModel,
    lazyListState: LazyListState,
) {
    var settingDataStore: UserPreferences
    val localContext = LocalContext.current
    settingDataStore = UserPreferences(localContext)
    val currentTextColor = MaterialTheme.colorScheme.onBackground
    val textSize = remember { mutableStateOf(14f) }
    LaunchedEffect(Unit) {
        textSize.value = settingDataStore.fontSizeFlow.first()
    }
    val backgroundColorStr by settingDataStore.backgroundFlow.collectAsState(initial = "")
    val textColorStr by settingDataStore.textColorFlow.collectAsState(initial = "")

    val textColor = remember { mutableStateOf(currentTextColor) }
    textColor.value = if (textColorStr == "light") {
        lightColorScheme().onBackground
    } else {
        darkColorScheme().onBackground
    }


    SelectionContainer {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            items(
                count = viewModel.state.book!!.chapters.size,
                key = { index -> viewModel.state.book!!.chapters[index].hashCode() }
            ) { index ->
                val chapter = viewModel.state.book!!.chapters[index]
                ChapterLazyItemItem(
                    chapter = chapter,
                    state = viewModel.state,
                    textColor = textColor.value,
                    textSize = textSize.value,
                    onClick = {
                            viewModel.toggleReaderMenu()
                    }
                )
            }
        }
    }


}

@Composable
private fun ChapterLazyItemItem(
    chapter: Chapter,
    state: ReaderScreenState,
    onClick: () -> Unit,
    textColor: Color,
    textSize: Float
) {
    val paragraphs = remember { chunkText(chapter.content) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ onClick() }
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 10.dp),
            text = chapter.name,
            fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
            color = textColor
        )
        Spacer(modifier = Modifier.height(12.dp))

        paragraphs.forEach { para->
            Text(
                text = para,
                style = LocalTextStyle.current.copy(
                    fontSize = textSize.sp,
                    fontFamily = state.fontFamily.fontFamily,
                    color = textColor,
                ),
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
            )
        }

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        )
    }
}
