package com.example.mobdev2.ui.screens.book

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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobdev2.CachingResults
import com.example.mobdev2.R
import com.example.mobdev2.repo.BookRepository
import com.example.mobdev2.repo.ReaderDataRepository
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.ReaderData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.time.Instant
import java.time.ZoneId
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
    data object Georgia : ReaderFont("cursive", "Georgia", FontFamily(Font(R.font.georgia)))
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
    private val db: FirebaseFirestore,
    private val settingDataStore: UserPreferences
) : ViewModel() {

    var state by mutableStateOf(
        ReaderScreenState(
            fontFamily = ReaderFont.System,
            fontSize = 14f,
        )
    )
    init {
        viewModelScope.launch {
            settingDataStore.fontFlow.collect { fontName ->
                val font = ReaderFont.getFontByName(fontName)
                state = state.copy(fontFamily = font)
            }
        }
    }
    private var sessionStartTime = 0L

    private var _sessionDuration = MutableLiveData<Long>()
    val sessionDuration: LiveData<Long> get() = _sessionDuration

    var chaptersState by mutableStateOf(ChaptersScreenState())

    private val _chapterScrolledPercent = mutableFloatStateOf(0f)
    val chapterScrolledPercent: State<Float> = _chapterScrolledPercent

    private val _visibleChapterIndex = mutableIntStateOf(0)
    val visibleChapterIndex: State<Int> = _visibleChapterIndex

    private val _chapterSize = mutableIntStateOf(0)
    val chapterSize: State<Int> = _chapterSize

    private val endIdx = savedStateHandle.getStateFlow("endIdx", 0)

    private val email = FirebaseAuth.getInstance().currentUser?.email
    private val userID = email?.indexOf('@')?.let { email.substring(0, it) }

    val isPlayingAudio = savedStateHandle.getStateFlow("isPlayingAudio", false)
    private val audioUrl = savedStateHandle.getStateFlow("audioUrl", "")

    private lateinit var mediaPlayer: MediaPlayer
    private val  _bookID = mutableStateOf("")

    private val startIdx = savedStateHandle.getStateFlow("startIdx", 0)
    val expandMenu = savedStateHandle.getStateFlow("expandMenu", false)
    val chaptersContent = savedStateHandle.getStateFlow("chaptersContent", listOf<AnnotatedString>())
    val readingAction = savedStateHandle.getStateFlow("readingAction", "Read")
    private val highlights = savedStateHandle.getStateFlow("highlights", CachingResults.highlights)

    fun setChapterSize(chapterSize: Int) {
        _chapterSize.intValue = chapterSize
        savedStateHandle["chaptersContent"] = List(chapterSize) {AnnotatedString("")}
        if(CachingResults.highlights.size < chapterSize) {
            CachingResults.highlights = List(chapterSize) { listOf() }
            savedStateHandle["highlights"] = List(chapterSize) { listOf<Int>() }
        }
    }

    fun startSession() {
        sessionStartTime = System.currentTimeMillis()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun endSession() {
        val sessionEndTime = System.currentTimeMillis()
        // count the session duration in minutes
        val sessionDuration = (sessionEndTime - sessionStartTime) / 1000 / 60
        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(userID!!)

        viewModelScope.launch {
            try {
                userDocumentRef.get().addOnSuccessListener { document ->
                    var lastUpdate = 0L
                    if (document.get("lastUpdate") != null){
                        lastUpdate = document.get("lastUpdate") as Long
                    }
                    var session = document.get("session") as Long
                    val lastUpdateDate = Instant.ofEpochMilli(lastUpdate).atZone(ZoneId.systemDefault()).toLocalDate()
                    val currentDate = Instant.ofEpochMilli(sessionEndTime).atZone(ZoneId.systemDefault()).toLocalDate()
                    session += sessionDuration
                    Log.d("SESSION", "LAST UPDATE: $lastUpdateDate")
                    Log.d("SESSION", "SESSION: $currentDate")
                    Log.d("SESSION", "SESSION: $session")
                    if (lastUpdateDate != currentDate) {
                        userDocumentRef.set(hashMapOf("session" to sessionDuration, "lastUpdate" to sessionEndTime), SetOptions.merge())
                    } else {
                        userDocumentRef.set(hashMapOf("session" to session, "lastUpdate" to sessionEndTime), SetOptions.merge())
                    }

                }
            } catch (e: Exception) {
                Log.e("UPDATE SESSION", "FAILED: $e")
            }
        }
    }

    fun addHighlight() {
        val mutableList = chaptersContent.value.toMutableList()
        mutableList[visibleChapterIndex.value] = buildAnnotatedString {
            append(chaptersContent.value[visibleChapterIndex.value])
            val leftIdx = min(startIdx.value, endIdx.value)
            val rightIdx = max(startIdx.value, endIdx.value)
            addStyle(SpanStyle(color = Color.Magenta), leftIdx, rightIdx)
            addStyle(SpanStyle(background = Color.Yellow), leftIdx, rightIdx)
        }
        savedStateHandle["chaptersContent"] = mutableList.toList()
    }

    fun changeReadingAction(action: String) {
        savedStateHandle["readingAction"] = action
    }

    fun setStartIdx(startIdx: Int) {
        savedStateHandle["startIdx"] = startIdx
    }

    fun setBookID(bookID: String) {
        _bookID.value = bookID
    }

    private fun updateHighlightDB() = viewModelScope.launch(Dispatchers.IO) {
        val docRef = db.collection("users").document(CachingResults.currentUser.name).collection("books").document(_bookID.value)
        val stringList = highlights.value.map { it.joinToString(separator = ",") }
        val updates = hashMapOf<String, Any>(
            "highlights" to stringList
        )
        docRef.update(updates)
            .addOnSuccessListener {
                Log.d("UPDATE", "HIGHLIGHT SUCCESSFUL")
            }
            .addOnFailureListener { e ->
                Log.d("UPDATE FAILED", "$e")
            }
    }

    fun loadChaptersContent(chapterContent: String) {
        val mutableList = chaptersContent.value.toMutableList()
        for(i in 0..<chapterSize.value) {
            mutableList[i] = buildAnnotatedString {
                append(chapterContent)
                for (j in highlights.value[i].indices) {
                    if (j % 2 == 0) {
                        addStyle(SpanStyle(color = Color.Magenta), highlights.value[i][j], highlights.value[i][j + 1])
                        addStyle(SpanStyle(background = Color.Yellow), highlights.value[i][j], highlights.value[i][j + 1])
                    }
                }
            }
        }
        savedStateHandle["chaptersContent"] = mutableList.toList()
    }



    fun setEndIdx(endIdx: Int) {
        savedStateHandle["endIdx"] = endIdx
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

    fun toggleMenu() {
        savedStateHandle["expandMenu"] = !expandMenu.value
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

    fun addHighlightToArray() {
        val startIdx = startIdx.value
        val endIdx = endIdx.value
        val mutableList = highlights.value.toMutableList()
        mutableList[visibleChapterIndex.value] = insertRange(highlights.value[visibleChapterIndex.value].toMutableList(), startIdx, endIdx)
        savedStateHandle["highlights"] = mutableList.toList()
        CachingResults.highlights = mutableList.toList()
        updateHighlightDB()
    }

    fun removeHighlightFromArray(removeIdx: Int) {
        val mutableList = highlights.value.toMutableList()
        mutableList[visibleChapterIndex.value] = removeRange(highlights.value[visibleChapterIndex.value], removeIdx)
        savedStateHandle["highlights"] = mutableList.toList()
        CachingResults.highlights = mutableList.toList()
        updateHighlightDB()
        Log.d("REMOVED HIGHLIGHT ARRAY", highlights.value.joinToString(", "))
    }

    fun reload() {
        val mutableContentList = chaptersContent.value.toMutableList()
        mutableContentList[visibleChapterIndex.value] = buildAnnotatedString {
            append(chaptersContent.value[visibleChapterIndex.value])
            for (j in highlights.value[visibleChapterIndex.value].indices) {
                if (j % 2 == 0) {
                    addStyle(SpanStyle(color = Color.Magenta), highlights.value[visibleChapterIndex.value][j], highlights.value[visibleChapterIndex.value][j + 1])
                    addStyle(SpanStyle(background = Color.Yellow), highlights.value[visibleChapterIndex.value][j], highlights.value[visibleChapterIndex.value][j + 1])
                }
            }
        }
        savedStateHandle["chaptersContent"] = mutableContentList.toList()
    }

    fun setVisibleChapterIndex(index: Int) {
        _visibleChapterIndex.intValue = index
    }

    fun setChapterScrollPercent(percent: Float) {
        _chapterScrolledPercent.floatValue = percent
    }

    private fun insertRange(ranges: MutableList<Int>, newStart: Int, newEnd: Int): List<Int> {
        var insertionIndex = 0
        while (insertionIndex < ranges.size && ranges[insertionIndex] <= newStart) {
            insertionIndex += 2
        }

        if (insertionIndex > 0) {
            val previousEnd = ranges[insertionIndex - 1]
            if (previousEnd >= newStart) {
                // Overlap with previous range
                if (newEnd > previousEnd) {
                    ranges[insertionIndex - 1] = newEnd
                }
                return ranges
            }
        }

        if (insertionIndex < ranges.size && ranges[insertionIndex] <= newEnd) {
            // Overlap with next range
            if (newStart < ranges[insertionIndex]) {
                ranges[insertionIndex] = newStart
            }
            return ranges
        }

        ranges.add(insertionIndex, newStart)
        ranges.add(insertionIndex + 1, newEnd)
        return ranges.toList()
    }

    private fun removeRange(ranges: List<Int>, number: Int): List<Int> {
        val newRanges = mutableListOf<Int>()
        var i = 0
        while (i < ranges.size) {
            if (i + 1 < ranges.size && number >= ranges[i] && number <= ranges[i + 1]) {
                i += 2
            } else {
                newRanges.add(ranges[i])
                i++
            }
        }
        return newRanges.toList()
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