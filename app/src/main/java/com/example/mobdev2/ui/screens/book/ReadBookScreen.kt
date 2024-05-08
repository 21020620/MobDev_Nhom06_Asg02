package com.example.mobdev2.ui.screens.book

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.ReadBookSettingsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


object ReaderConstants {
    const val DEFAULT_NONE = -100000

}

@Destination
@BookNavGraph
@Composable
fun ReadBookScreen(
    bookID: String,
    viewModel: ReadBookViewModel = koinViewModel(),
    navigator: DestinationsNavigator,
) {
    // Hide reader menu on back press.
    BackHandler(viewModel.state.showReaderMenu) {
        viewModel.hideReaderInfo()
    }

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    var isBookLoaded by remember { mutableStateOf(false) }


    val scrollToPosition = { index: Int, offset: Int ->
        coroutineScope.launch {
            lazyListState.scrollToItem(index, offset)
        }
    }

    val navigateToPreviousChapter: () -> Unit = {
        val currentChapterIndex = viewModel.visibleChapterIndex.value
        if (currentChapterIndex > 0) {
            coroutineScope.launch {
                viewModel.setVisibleChapterIndex(currentChapterIndex - 1)
                lazyListState.scrollToItem(currentChapterIndex - 1)
            }
        }
    }

    val navigateToNextChapter: () -> Unit = {
        val currentChapterIndex = viewModel.visibleChapterIndex.value
        val totalChapters = viewModel.state.book?.chapters?.size ?: 0
        if (currentChapterIndex < totalChapters - 1) {
            coroutineScope.launch {
                viewModel.setVisibleChapterIndex(currentChapterIndex + 1)
                lazyListState.scrollToItem(currentChapterIndex + 1)
            }
        }
    }

    LaunchedEffect(bookID) {
        viewModel.loadBook(bookID = bookID, onLoaded = {
            // if there is saved progress for this book, then scroll to
            // last page at exact position were used had left.
            if (it.readerData != null && it.readerData.lastChapterIndex != ReaderConstants.DEFAULT_NONE) {
                scrollToPosition(it.readerData.lastChapterIndex, it.readerData.lastChapterOffset)
            }
            isBookLoaded = true
        })

        if (viewModel.state.readerData?.lastChapterIndex != null && viewModel.state.readerData?.lastChapterIndex != ReaderConstants.DEFAULT_NONE) {
            scrollToPosition(viewModel.state.readerData?.lastChapterIndex!!, 0)
        }


    }

    LaunchedEffect(lazyListState, isBookLoaded) {
        if (isBookLoaded) {
            snapshotFlow {
                lazyListState.firstVisibleItemScrollOffset
            }.collect { visibleChapterOffset ->
                // fetch last visible chapter position and offset.
                val visibleChapterIdx = lazyListState.firstVisibleItemIndex
                // Set currently visible chapter & index.
                viewModel.setVisibleChapterIndex(visibleChapterIdx)
                viewModel.setChapterScrollPercent(
                    viewModel.calculateChapterPercentage(lazyListState)
                )

                // update the reading progress into the database.
                viewModel.updateReaderProgress(
                    bookId = "Z7sXjKwP6XL46c2CNW54",
                    chapterIndex = visibleChapterIdx,
                    chapterOffset = visibleChapterOffset
                )

            }
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val currentChapter =
        viewModel.state.book?.chapters?.getOrNull(viewModel.visibleChapterIndex.value)

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val chapters = viewModel.state.book?.chapters

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Chapters",
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    
                    color = MaterialTheme.colorScheme.onSurface
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

                chapters?.let { chapters ->
                    LazyColumn {
                        items(chapters.size) { idx ->
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = chapters[idx].name,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                selected = idx == viewModel.visibleChapterIndex.value,
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.close()
                                        lazyListState.scrollToItem(idx)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }) {

        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                AnimatedVisibility(
                    visible = viewModel.state.showReaderMenu,
                    enter = expandVertically(initialHeight = { 0 }, expandFrom = Alignment.Top)
                            + fadeIn(),
                    exit = shrinkVertically(targetHeight = { 0 }, shrinkTowards = Alignment.Top)
                            + fadeOut(),
                ) {
                    Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)) {
                        Column(modifier = Modifier.displayCutoutPadding()) {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                ),
                                title = {
                                    viewModel.state.book?.let {
                                        Text(
                                            text = it.title,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.animateContentSize(),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(
                                            Icons.Filled.Search, null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(
                                            Icons.Filled.AutoFixNormal, null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            )
                            Column(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 16.dp),
                            ) {
                                currentChapter?.name?.let {
                                    Text(
                                        text = it,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            val chapterProgressbarState = animateFloatAsState(
                                targetValue = viewModel.chapterScrolledPercent.value,
                                label = "chapter progress bar state animation"
                            )
                            LinearProgressIndicator(
                                progress = { chapterProgressbarState.value },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = viewModel.state.showReaderMenu,
                    enter = expandVertically(initialHeight = { 0 }) + fadeIn(),
                    exit = shrinkVertically(targetHeight = { 0 }) + fadeOut(),
                ) {
                    BottomBar(
                        onNavigateBeforeClick = { navigateToPreviousChapter() },
                        onSettingsClick = {
                            navigator.navigate(ReadBookSettingsDestination)
                        },
                        onPlayStopAudioClick = { /*TODO*/ },
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onNavigateNextClick = { navigateToNextChapter() }
                    )
                }
            },
            content = { paddingValues ->
                Crossfade(
                    targetState = viewModel.state.isLoading,
                    label = "reader content loading cross fade"
                ) { loading ->
                    if (loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 65.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        Box(modifier = Modifier.padding(paddingValues)) {
                            ReaderContent(viewModel = viewModel, lazyListState = lazyListState)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun BottomBar(
    onNavigateBeforeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPlayStopAudioClick: () -> Unit,
    onMenuClick: () -> Unit,
    onNavigateNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        IconButton(onClick = { onNavigateBeforeClick() }, modifier = Modifier.weight(1f)) {
            Icon(
                Icons.Filled.NavigateBefore, null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(50.dp)
            )
        }

        IconButton(onClick = { onSettingsClick() }, modifier = Modifier.weight(1f)) {
            Icon(
                Icons.Outlined.Settings, null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(onClick = { onPlayStopAudioClick() }, modifier = Modifier.weight(1f)) {
            Icon(
                Icons.Filled.PlayCircleOutline, null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(onClick = { onMenuClick() }, modifier = Modifier.weight(1f)) {
            Icon(
                Icons.Filled.Menu, null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(onClick = { onNavigateNextClick() }, modifier = Modifier.weight(1f)) {
            Icon(
                Icons.Filled.NavigateNext, null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

