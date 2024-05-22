package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.first

@Composable
fun ReaderContent(
    viewModel: ReadBookViewModel,
    lazyListState: LazyListState,
) {
    var settingDataStore: UserPreferences
    val localContext = LocalContext.current
    settingDataStore = UserPreferences(localContext)
    val currentTextColor = MaterialTheme.colorScheme.onBackground
    val textSize = remember { mutableFloatStateOf(14f) }
    LaunchedEffect(Unit) {
        textSize.floatValue = settingDataStore.fontSizeFlow.first()
    }
    val isLoading = remember {
        mutableStateOf(true)
    }
    val textColorStr by settingDataStore.textColorFlow.collectAsState(initial = "")

    val textColor = remember { mutableStateOf(currentTextColor) }
    textColor.value = if (textColorStr == "light") {
        lightColorScheme().onBackground
    } else {
        darkColorScheme().onBackground
    }

    viewModel.setChapterSize(viewModel.state.book!!.chapters.size)

    val chaptersContentInitial = viewModel.state.book!!.chapters.map { it.content }
    viewModel.loadChaptersContent(chaptersContentInitial)
    isLoading.value = false

    val chaptersContent = viewModel.chaptersContent.collectAsState()

    if (isLoading.value) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            items(
                count = viewModel.state.book!!.chapters.size,
                key = { index -> viewModel.state.book!!.chapters[index].name }
            ) { index ->
                val chapter = viewModel.state.book!!.chapters[index]
                Log.d("Content here", chaptersContent.value[index].toString())
                ChapterLazyItem(
                    chapterName = chapter.name,
                    chapterContent = viewModel.highlightSearchResults(chaptersContent.value[index], index),
                    state = viewModel.state,
                    textColor = textColor.value,
                    textSize = textSize.value,
                    viewModel = viewModel
                )
            }
        }
    }
}

fun ReadBookViewModel.highlightSearchResults(content: AnnotatedString, chapterIndex: Int): AnnotatedString {
    if (searchQuery.isEmpty()) return content

    val chapterResults = searchResults.getOrNull(chapterIndex) ?: return content

    val highlightedContent = buildAnnotatedString {
        append(content)
        chapterResults.forEach { index ->
            addStyle(
                style = SpanStyle(background = Color.Yellow),
                start = index,
                end = index + searchQuery.length
            )
        }
    }
    return highlightedContent
}


@Composable
private fun ChapterLazyItem(
    chapterName: String,
    chapterContent: AnnotatedString,
    state: ReaderScreenState,
    textColor: Color,
    viewModel: ReadBookViewModel,
    textSize: Float
) {
    val readingAction = viewModel.readingAction.collectAsState()
    val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        viewModel.toggleReaderMenu()
                    }
                )
            }
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 10.dp),
            text = chapterName,
            fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
            color = textColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = chapterContent,
            onTextLayout = { layoutResult ->
                textLayoutResult.value = layoutResult
            },
            style = LocalTextStyle.current.copy(
                fontSize = textSize.sp,
                fontFamily = state.fontFamily.fontFamily,
                color = textColor,
            ),
            textAlign = TextAlign.Justify,
            modifier = when (readingAction.value) {
                "Read" -> Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp)
                "Highlight" -> Modifier
                    .padding(start = 14.dp, end = 14.dp, bottom = 8.dp)
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                textLayoutResult.value?.let { layoutResult ->
                                    val characterOffset = layoutResult.getOffsetForPosition(offset)
                                    val wordRange = layoutResult.getWordBoundary(characterOffset)
                                    viewModel.setStartIdx(wordRange.start)
                                    viewModel.setEndIdx(wordRange.end)
                                    viewModel.addHighlight()
                                    Log.d("START INDEX", "${wordRange.start}")
                                }
                            },
                            onDrag = { change, _ ->
                                textLayoutResult.value?.let { layoutResult ->
                                    val characterOffset = layoutResult.getOffsetForPosition(change.position)
                                    val wordRange = layoutResult.getWordBoundary(characterOffset)
                                    Log.d("END INDEX", "${wordRange.end}")
                                    viewModel.setEndIdx(wordRange.end)
                                    viewModel.addHighlight()
                                }
                                change.consume()
                            },
                            onDragEnd = {
                                Log.d("DRAG ENDED", "NOTHING")
                                viewModel.addHighlightToArray()
                                viewModel.changeReadingAction("Read")
                            }
                        )
                    }

                else -> Modifier
                    .padding(start = 14.dp, end = 14.dp, bottom = 8.dp)
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                textLayoutResult.value?.let { layoutResult ->
                                    val characterOffset = layoutResult.getOffsetForPosition(offset)
                                    val wordRange = layoutResult.getWordBoundary(characterOffset)
                                    Log.d("REMOVE HIGHLIGHT", "${wordRange.start}")
                                    viewModel.removeHighlightFromArray(wordRange.start)
                                }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                            },
                            onDragEnd = {
                                Log.d("DRAG ENDED", "NOTHING")
                                viewModel.toggleReaderMenu()
                                viewModel.changeReadingAction("Read")
                            }
                        )
                    }
            },
        )

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        )
    }
}
