package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@BookNavGraph
@Destination
@Composable
fun BookForumScreen() {
    val text = "Hello, World! This is a test sentence. Click on text. Select text"
    val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    SelectionContainer {
        Text(
            text = text,
            onTextLayout = { layoutResult ->
                textLayoutResult.value = layoutResult
            },
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset ->
                        Log.d("Tap Event", "Tap detected at position: $offset")
                        textLayoutResult.value?.let { layoutResult ->
                            val characterOffset = layoutResult.getOffsetForPosition(offset)
                            val wordRange = layoutResult.getWordBoundary(characterOffset)
                            Log.d("Tapped Word", "Word range: ${wordRange.start} - ${wordRange.end}")
                        }
                    })
                }
        )
    }
}