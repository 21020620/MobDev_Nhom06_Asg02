package com.example.mobdev2.ui.screens.book

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ReadBookViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    val selectionState = savedStateHandle.getStateFlow("selectionState", 0)
    val startIdx = savedStateHandle.getStateFlow("startIdx", 0)
    val content = savedStateHandle.getStateFlow("content", AnnotatedString("This is a test sentence for highlighting function of the app. Extra words to make sentence longer. Make this text highlight. Longer text for better visualization"))
    val selectionStateString = savedStateHandle.getStateFlow("selectionStateString", "")
    fun addHighlight(endIdx: Int) {
        savedStateHandle["content"] = buildAnnotatedString {
            append(content.value)
            addStyle(SpanStyle(color = Color.Magenta), startIdx.value, endIdx)
            addStyle(SpanStyle(background = Color.Yellow), startIdx.value, endIdx)
        }
        savedStateHandle["selectionState"] = 0
        savedStateHandle["selectionStateString"] = ""
    }

    fun setStartIdx(startIdx: Int) {
        savedStateHandle["startIdx"] = startIdx
        savedStateHandle["selectionState"] = 2
        savedStateHandle["selectionStateString"] = "Pick ending word..."
    }

    fun startHighlight() {
        savedStateHandle["selectionState"] = 1
        savedStateHandle["selectionStateString"] = "Pick starting word..."
    }
}