package com.example.mobdev2.ui.screens.book

import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pentagon
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Pentagon
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.koinViewModel

@BookNavGraph
@Destination
@Composable
fun BookForumScreen(
    viewModel: ReadBookViewModel = koinViewModel()
) {
    val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val text = viewModel.content.collectAsState()
    val selectionState = viewModel.selectionState.collectAsState()
    val chapterName = "Chapter 4"
    val selectionStateString = viewModel.selectionStateString.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "$chapterName  ${selectionStateString.value}")
                },
                actions = {
                    IconButton(onClick = viewModel::startHighlight) {
                        Icon(
                            imageVector =
                            if(selectionState.value == 1 || selectionState.value == 2) Icons.Default.Pentagon
                            else Icons.Outlined.Pentagon,
                            contentDescription = "Start Highlighting",
                        )
                    }
                }
            )
        }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            Text(
                text = text.value,
                onTextLayout = { layoutResult ->
                    textLayoutResult.value = layoutResult
                },
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(paddingValue)
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                textLayoutResult.value?.let { layoutResult ->
                                    val characterOffset = layoutResult.getOffsetForPosition(offset)
                                    val wordRange = layoutResult.getWordBoundary(characterOffset)
                                    viewModel.setStartIdx(wordRange.start)
                                    viewModel.addHighlight(wordRange.end)
                                    Log.d("START INDEX", "${wordRange.start}")
                                }
                            },
                            onDrag = { change, _ ->
                                textLayoutResult.value?.let { layoutResult ->
                                    val characterOffset = layoutResult.getOffsetForPosition(change.position)
                                    val wordRange = layoutResult.getWordBoundary(characterOffset)
                                    viewModel.addHighlight(wordRange.end)
                                }
                                change.consume()
                            },
                            onDragEnd = {
                                Log.d("DRAG ENDED", "NOTHING")
                            }
                        )
                    }
            )
        }
    }
}