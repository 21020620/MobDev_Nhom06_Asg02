package com.example.mobdev2.ui.screens.book

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.R
import com.example.mobdev2.repo.BookRepository
import com.example.mobdev2.repo.ReaderDataRepository
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.ReaderData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@Keep
sealed class ReaderFont(val id: String, val name: String, val fontFamily: FontFamily) {

    companion object {
        fun getAllFonts() =
            ReaderFont::class.sealedSubclasses.mapNotNull { it.objectInstance }.sortedBy { it.name }

        fun getFontByName(name: String) = getAllFonts().find { it.name == name }!!
    }

    @Keep
    data object System : ReaderFont("system", "System Default", FontFamily.Default)

    @Keep
    data object Georgia : ReaderFont("cursive", "Cursive", FontFamily(Font(R.font.georgia)))
    @Keep
    data object OpenDyslexic :
        ReaderFont("openDyslexic", "OpenDyslexic", FontFamily(Font(R.font.open_dyslexic_regular)))

    @Keep
    data object Times : ReaderFont("times", "Times New Roman", FontFamily(Font(R.font.times)))

    @Keep
    data object Sans : ReaderFont("sans", "SansSerif", FontFamily(Font(R.font.sans)))
}

data class ReaderScreenState(
    val isLoading: Boolean = true,
    val showReaderMenu: Boolean = false,
    val fontFamily: ReaderFont = ReaderFont.System,
    val fontSize: Float = 14f,
    val background: String? = "current",
    val textColor: String? = "current",
    val book: Book? = null,
    val readerData: ReaderData? = null
)

data class ChaptersScreenState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val readerData: ReaderData? = null
)


@KoinViewModel
class ReadBookViewModel(
    private val bookRepo: BookRepository,
    private val readerDataRepo: ReaderDataRepository,
    private val savedStateHandle: SavedStateHandle,
    private val settingDataStore: UserPreferences
) : ViewModel() {

    var state by mutableStateOf(
        ReaderScreenState(
            fontFamily = ReaderFont.System,
            fontSize = 14f,
            background = "current",
            textColor = "current"
        )
    )
    init {
        viewModelScope.launch {
            settingDataStore.fontFlow.collect { font ->
                val readerFont = if (font != null && ReaderFont.getAllFonts().any { it.name == font }) {
                    ReaderFont.getFontByName(font)
                } else {
                    ReaderFont.System
                }
                state = state.copy(fontFamily = readerFont)
            }
            settingDataStore.fontSizeFlow.collect { fontSize ->
                Log.d("FONTSIZE","$fontSize")
                state = state.copy(fontSize = fontSize)
                Log.d("FONTSIZE","${state.fontSize}")
            }
            settingDataStore.backgroundFlow.collect { bg ->
                Log.d("BACKGROUND", "$bg")
                state = state.copy(background = bg)
                Log.d("BackgroundAfter", "${state.background}")
            }
            settingDataStore.textColorFlow.collect { textColor ->
                Log.d("TEXTCOLOR", "$textColor")
                state = state.copy(textColor = textColor)
                Log.d("TextColorAfter", "${state.textColor}")
            }
        }
    }

    var chaptersState by mutableStateOf(ChaptersScreenState())

    private val _chapterScrolledPercent = mutableFloatStateOf(0f)
    val chapterScrolledPercent: State<Float> = _chapterScrolledPercent

    private val _visibleChapterIndex = mutableIntStateOf(0)
    val visibleChapterIndex: State<Int> = _visibleChapterIndex


    private val email = FirebaseAuth.getInstance().currentUser?.email
    private val userID = email?.indexOf('@')?.let { email?.substring(0, it) }


    val selectionState = savedStateHandle.getStateFlow("selectionState", 0)
    val startIdx = savedStateHandle.getStateFlow("startIdx", 0)
    val content = savedStateHandle.getStateFlow(
        "content",
        AnnotatedString("This is a test sentence for highlighting function of the app. Extra words to make sentence longer. Make this text highlight. Longer text for better visualization")
    )
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

    fun loadBook(bookID: String, onLoaded: (ReaderScreenState) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedBook = bookRepo.getBookByID(bookID)
                val readerData = userID?.let { readerDataRepo.getReaderData(it, bookID) }
                state = state.copy(book = fetchedBook, readerData = readerData)
                // Added some delay to avoid choppy animation.
                delay(350L)
                onLoaded(state)
                state = state.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e("FETCH DATA FAILURE", "$e")
            }

        }
    }

    fun loadReaderData(bookID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val readerData = userID?.let { readerDataRepo.getReaderData(it, bookID) }
                // Added some delay to avoid choppy animation.
                delay(350L)
                chaptersState = chaptersState.copy(isLoading = false, readerData = readerData)
            } catch (e: Exception) {
                Log.e("FETCH DATA FAILURE", "$e")
            }

        }
    }

    fun toggleReaderMenu() {
        state = if (state.showReaderMenu) {
            state.copy(showReaderMenu = false)
        } else {
            state.copy(showReaderMenu = true)
        }
    }

    fun setVisibleChapterIndex(index: Int) {
        _visibleChapterIndex.intValue = index
    }

    fun setChapterScrollPercent(percent: Float) {
        _chapterScrolledPercent.floatValue = percent
    }

    fun updateReaderProgress(bookId: String, chapterIndex: Int, chapterOffset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (userID?.let { readerDataRepo.getReaderData(it, bookId) } != null) {
                val updates = mapOf(
                    "lastChapterIndex" to chapterIndex,
                    "lastChapterOffset" to chapterOffset
                )
                readerDataRepo.updateReaderData(userID, bookId, updates)
            } else {
                userID?.let {
                    ReaderData(bookId, chapterIndex, chapterOffset, it)
                }?.let { readerDataRepo.saveReaderData(readerData = it) }
            }
        }
    }

    fun hideReaderInfo() {
        state = state.copy(showReaderMenu = false)
    }

    fun calculateChapterPercentage(lazyListState: LazyListState): Float {
        val firstVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull() ?: return -1f
        val listHeight =
            lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset

        // Calculate the scroll percentage for the first visible item
        val itemTop = firstVisibleItem.offset.toFloat()
        val itemBottom = itemTop + firstVisibleItem.size.toFloat()

        return if (itemTop >= listHeight || itemBottom <= 0f) {
            1f // Item is completely scrolled out of view
        } else {
            // Calculate the visible portion of the item
            val visiblePortion = if (itemTop < 0f) {
                itemBottom
            } else {
                listHeight - itemTop
            }
            // Calculate the scroll percentage based on the visible portion
            ((1f - visiblePortion / firstVisibleItem.size.toFloat())).coerceIn(0f, 1f)
        }
    }


}