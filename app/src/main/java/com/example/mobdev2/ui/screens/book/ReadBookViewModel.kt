package com.example.mobdev2.ui.screens.book

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.math.max
import kotlin.math.min
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
    val fontSize: Int = 18,
    val book: Book? = null,
    val readerData: ReaderData? = null
)

data class ChaptersScreenState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val readerData: ReaderData? = null
)

data class UserPreferences(
    val fontFamily: String,
    val fontSize: Int
)

private const val USER_PREFERENCES_NAME = "user_preferences"

//private val Context.dataStore by dataStore(
//    fileName = "user_preferences",
//    serializer = MyCustomSerializer,
//)
//class UserPreferencesRepository(
//    private val context: DataStore<Preferences>
//) {
//    private val dataStore = context.dataStore
//    private val FONT_KEY = stringPreferencesKey("font")
//    private val FONT_SIZE_KEY = intPreferencesKey("font_size")
//
//    suspend fun saveFontSize(fontSize: Int) {
//        dataStore.edit { preferences ->
//            preferences[FONT_SIZE_KEY] = fontSize
//        }
//    }
//
//    suspend fun saveFontFamily(fontFamily: String) {
//        dataStore.edit { preferences ->
//            preferences[FONT_KEY] = fontFamily
//        }
//    }
//}

@KoinViewModel
class ReadBookViewModel(
    private val bookRepo: BookRepository,
    private val readerDataRepo: ReaderDataRepository,
    private val savedStateHandle: SavedStateHandle,
//    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var state by mutableStateOf(
        ReaderScreenState(
            fontFamily = ReaderFont.System,
            fontSize = 14
        )
    )

    var chaptersState by mutableStateOf(ChaptersScreenState())

    private val _chapterScrolledPercent = mutableFloatStateOf(0f)
    val chapterScrolledPercent: State<Float> = _chapterScrolledPercent

    private val _visibleChapterIndex = mutableIntStateOf(0)
    val visibleChapterIndex: State<Int> = _visibleChapterIndex


    private val email = FirebaseAuth.getInstance().currentUser?.email
    private val userID = email?.indexOf('@')?.let { email?.substring(0, it) }

    val isPlayingAudio = savedStateHandle.getStateFlow("isPlayingAudio", false)
    val audioUrl = savedStateHandle.getStateFlow("audioUrl", "")

    private lateinit var mediaPlayer: MediaPlayer

    val selectionState = savedStateHandle.getStateFlow("selectionState", 0)
    val startIdx = savedStateHandle.getStateFlow("startIdx", 0)
    val content = savedStateHandle.getStateFlow(
        "content",
        AnnotatedString("This is a test sentence for highlighting function of the app. Extra words to make sentence longer. Make this text highlight. Longer text for better visualization")
    )
    val selectionStateString = savedStateHandle.getStateFlow("selectionStateString", "")
//    private val FONT_KEY = stringPreferencesKey("font")
//    private val FONT_SIZE_KEY = intPreferencesKey("font_size")


//    fun setFontFamily(fontFamily: ReaderFont) {
//        state = state.copy(fontFamily = fontFamily)
//        viewModelScope.launch {
//            userPreferencesRepository.saveFontFamily(fontFamily.name)
//        }
//    }
//
//    fun increaseFontSize() {
//        val newFontSize = state.fontSize + 2
//        state = state.copy(fontSize = newFontSize)
//        viewModelScope.launch {
//            userPreferencesRepository.saveFontSize(newFontSize)
//        }
//    }
//
//    fun decreaseFontSize() {
//        val newFontSize = state.fontSize - 2
//        state = state.copy(fontSize = newFontSize)
//        viewModelScope.launch {
//            userPreferencesRepository.saveFontSize(newFontSize)
//        }
//    }
    fun addHighlight(endIdx: Int) {
        savedStateHandle["content"] = buildAnnotatedString {
            append(content.value)
            val leftIdx = min(startIdx.value, endIdx)
            val rightIdx = max(startIdx.value, endIdx)
            addStyle(SpanStyle(color = Color.Magenta), leftIdx, rightIdx)
            addStyle(SpanStyle(background = Color.Yellow), leftIdx, rightIdx)
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

    fun getAudioFile() {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child("synthesis.wav")
        fileRef.downloadUrl.addOnSuccessListener { uri ->
            savedStateHandle["audioUrl"] = uri.toString()
        }.addOnFailureListener {
            Log.e("AUDIO", "Failed to play audio: $it")
        }
    }

    fun toggleAudio() {
        if (isPlayingAudio.value) {
            savedStateHandle["isPlayingAudio"] = false
            mediaPlayer.stop()
            mediaPlayer.release()
        } else {
            savedStateHandle["isPlayingAudio"] = true
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl.value)
                prepare()
                start()
            }
        }
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

        fun startPlayingAudio() {
            savedStateHandle["isPlayingAudio"] = true
        }

        fun calculateChapterPercentage(lazyListState: LazyListState): Float {
            val firstVisibleItem =
                lazyListState.layoutInfo.visibleItemsInfo.firstOrNull() ?: return -1f
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