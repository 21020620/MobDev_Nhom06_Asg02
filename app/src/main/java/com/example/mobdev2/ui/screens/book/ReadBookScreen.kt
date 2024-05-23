package com.example.mobdev2.ui.screens.book

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.ReadBookSettingsDestination
import com.example.mobdev2.ui.theme.tertiaryContainerLight
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

object ReaderConstants {
    const val DEFAULT_NONE = -100000
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(FlowPreview::class)
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
    val isPlayingAudio = viewModel.isPlayingAudio.collectAsState()

    var settingDataStore: UserPreferences
    val localContext = LocalContext.current
    settingDataStore = UserPreferences(localContext)
    val currentBackground = MaterialTheme.colorScheme.background
    val backgroundColorStr by settingDataStore.backgroundFlow.collectAsState(initial = "")

    val backgroundColor = remember { mutableStateOf(currentBackground) }
    backgroundColor.value = when (backgroundColorStr) {
        "light" -> lightColorScheme().background
        "dark" -> darkColorScheme().background
        "tertiary" -> tertiaryContainerLight
        else -> currentBackground
    }

    val scrollToPosition: (Int, Int) -> Unit = { index, offset ->
        coroutineScope.launch {
            lazyListState.scrollToItem(index, offset)
        }
    }

    val expandMenu = viewModel.expandMenu.collectAsStateWithLifecycle()

    val navigateToPreviousChapter: () -> Unit = {
        val currentChapterIndex = viewModel.visibleChapterIndex.value
        if (currentChapterIndex > 0) {
            coroutineScope.launch {
                viewModel.setVisibleChapterIndex(currentChapterIndex - 1)
                viewModel.setChapterScrollPercent(0f)
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
                viewModel.setChapterScrollPercent(0f)
                lazyListState.scrollToItem(currentChapterIndex + 1)
            }
        }
    }

    LaunchedEffect(bookID) {
        viewModel.setScrollToPosition(scrollToPosition)
        viewModel.loadBook(bookID = bookID, onLoaded = {
            // if there is saved progress for this book, then scroll to
            // last page at exact position were used had left.
            if (it.readerData != null && it.readerData.lastChapterIndex != ReaderConstants.DEFAULT_NONE) {
                scrollToPosition(it.readerData.lastChapterIndex, it.readerData.lastChapterOffset)
            }
            isBookLoaded = true
        })
        viewModel.startSession()
        viewModel.setBookID(bookID)
        if (viewModel.state.readerData?.lastChapterIndex != null && viewModel.state.readerData?.lastChapterIndex != ReaderConstants.DEFAULT_NONE) {
            scrollToPosition(viewModel.state.readerData?.lastChapterIndex!!, 0)
        }
    }

    DisposableEffect(bookID) {
        onDispose {
            viewModel.endSession()
        }
    }

    val updateFlow = snapshotFlow { lazyListState.firstVisibleItemScrollOffset }

    LaunchedEffect(lazyListState, isBookLoaded) {
        launch {
            if (isBookLoaded)
                updateFlow
                    .debounce(400)
                    .collect { visibleChapterOffset ->
                        val visibleChapterIdx = lazyListState.firstVisibleItemIndex
                        viewModel.updateReaderProgress(
                            bookId = bookID,
                            chapterIndex = visibleChapterIdx,
                            chapterOffset = visibleChapterOffset
                        )
                    }
        }
        if (isBookLoaded) {
            updateFlow.collect { _ ->
                val visibleChapterIdx = lazyListState.firstVisibleItemIndex
                viewModel.setVisibleChapterIndex(visibleChapterIdx)
                viewModel.setChapterScrollPercent(
                    viewModel.calculateChapterPercentage(lazyListState)
                )
            }
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }

    var currentChapter =
        viewModel.state.book?.chapters?.getOrNull(viewModel.visibleChapterIndex.value)?.name

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val chapters = viewModel.state.book?.chapters

    var showSearch by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.toggleAudio(lazyListState.firstVisibleItemIndex + 1)
        }
    }

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
                                        viewModel.setVisibleChapterIndex(idx)
                                        viewModel.setChapterScrollPercent(0f)
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
                                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        2.dp
                                    ),
                                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        2.dp
                                    ),
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
                                    if (!showSearch) {
                                        IconButton(onClick = { showSearch = true }) {
                                            Icon(
                                                Icons.Filled.Search, null,
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                    }

                                    if (showSearch) {
                                        SearchBar(
                                            viewModel = viewModel,
                                            onClose = { showSearch = false }
                                        )
                                    }

                                    IconButton(onClick = viewModel::toggleMenu) {
                                        Icon(
                                            Icons.Filled.AutoFixNormal, null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expandMenu.value,
                                        onDismissRequest = viewModel::toggleMenu
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(text = "Highlight") },
                                            onClick = {
                                                viewModel.changeReadingAction("Highlight")
                                                viewModel.toggleMenu()
                                                viewModel.toggleReaderMenu()
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.FormatPaint,
                                                    contentDescription = "Highlight"
                                                )
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(text = "Erase") },
                                            onClick = {
                                                viewModel.changeReadingAction("Erase")
                                                viewModel.toggleMenu()
                                                viewModel.toggleReaderMenu()
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.BookmarkRemove,
                                                    contentDescription = "Erase"
                                                )
                                            }
                                        )
                                    }
                                }
                            )

                            Column(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 16.dp),
                            ) {
                                currentChapter?.let {
                                    Text(
                                        text = it,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontStyle = MaterialTheme.typography.displayLarge.fontStyle,
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
                        onPlayStopAudioClick = { viewModel.toggleAudio(lazyListState.firstVisibleItemIndex + 1) },
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
                        Box(
                            modifier = Modifier
                                .padding(paddingValues)
                                .background(backgroundColor.value)
                        ) {
                            ReaderContent(viewModel = viewModel, lazyListState = lazyListState)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SearchBar(viewModel: ReadBookViewModel, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        TextField(
            value = viewModel.searchQuery,
            onValueChange = { query -> viewModel.searchQuery = query },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.performSearch(viewModel.searchQuery)
                keyboardController?.hide()
            }),
            placeholder = { Text("Search...") },
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { viewModel.previousSearchResult() }) {
            Icon(imageVector = Icons.Default.NavigateBefore, contentDescription = "Previous Result")
        }
        IconButton(onClick = { viewModel.nextSearchResult() }) {
            Icon(imageVector = Icons.Default.NavigateNext, contentDescription = "Next Result")
        }
        IconButton(onClick = { viewModel.clearSearch(); onClose() }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
        }
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
    val isPlayingAudio = remember { mutableStateOf(false) }
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

        IconButton(onClick = {
            onPlayStopAudioClick()
            isPlayingAudio.value = !isPlayingAudio.value
        }, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = if (!isPlayingAudio.value) Icons.Filled.PlayCircleOutline else Icons.Filled.PauseCircle,
                contentDescription = null,
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
