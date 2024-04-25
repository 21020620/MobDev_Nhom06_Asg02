package com.example.mobdev2.ui.screens.book

import android.view.ContextMenu
import android.view.MenuItem
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pentagon
import androidx.compose.material.icons.outlined.Pentagon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    val showContextMenu = remember { mutableStateOf(false) }
    val contextMenuPosition = remember { mutableStateOf(IntOffset.Zero) }
    val currentOffset = remember {
        mutableStateOf(Offset(0f, 0f))
    }
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
        Text(
            text = text.value,
            onTextLayout = { layoutResult ->
                textLayoutResult.value = layoutResult },
            fontSize = 30.sp,
            modifier = Modifier
                .padding(paddingValue)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            textLayoutResult.value?.let { layoutResult ->
                                val characterOffset = layoutResult.getOffsetForPosition(offset)
                                val wordRange = layoutResult.getWordBoundary(characterOffset)
                                if (selectionState.value == 2)
                                    viewModel.addHighlight(wordRange.end)
                            }
                        },
                        onLongPress = { offset ->
                            currentOffset.value = offset
                            contextMenuPosition.value = offset.round()
                            showContextMenu.value = true
                        }
                    )
                }
            )

        if (showContextMenu.value) {
            Popup(
                alignment = Alignment.TopStart,
                offset = contextMenuPosition.value,
                onDismissRequest = { showContextMenu.value = false }
            ) {
                DropdownMenu(
                    expanded = showContextMenu.value,
                    onDismissRequest = { /*TODO*/ }) {
                    DropdownMenuItem(
                        text = {  Text("Highlight") },
                        onClick = {
                            textLayoutResult.value?.let { layoutResult ->
                                val characterOffset = layoutResult.getOffsetForPosition(currentOffset.value)
                                val wordRange = layoutResult.getWordBoundary(characterOffset)
                                viewModel.setStartIdx(wordRange.start)
                                showContextMenu.value = false
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add comment") },
                        onClick = {  }
                    )
                }
            }
        }
    }
}