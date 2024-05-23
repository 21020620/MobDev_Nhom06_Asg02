package com.example.mobdev2.ui.screens.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdev2.CachingResults
import com.example.mobdev2.repo.AdvancedSearchRepo
import com.example.mobdev2.repo.model.ReaderData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AdvancedSearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val advancedSearchRepo: AdvancedSearchRepo
) : ViewModel() {
    val searchContent = savedStateHandle.getStateFlow("searchContent", "")
    val searchResults = savedStateHandle.getStateFlow("searchResults", mapOf<String, List<Int>>())

    fun setSearchContent(input: String) {
        savedStateHandle["searchContent"] = input
    }

    fun search() = viewModelScope.launch {
        val channel = Channel<Pair<String, Int>>()
        val searchResults: MutableMap<String, MutableList<Int>> = mutableMapOf()
        val wordsSearch = searchContent.value.split("\\s+".toRegex())
        val collectJob = launch {
            val resultList = mutableListOf<Pair<String, Int>>()
            channel.consumeAsFlow().collect { result ->
                resultList.add(result)
            }
            resultList.forEach { (bookId, chapterIndex) ->
                searchResults.getOrPut(bookId) { mutableListOf() }.add(chapterIndex)
            }
            savedStateHandle["searchResults"] = searchResults
        }

        val jobs = mutableListOf<Job>()
        for (i in CachingResults.bookList.indices) {
            val job = launch {
                val chapters = advancedSearchRepo.getChaptersOfBook(CachingResults.bookList[i].id)
                for (j in chapters.indices) {
                    val chapterContent = chapters[j].content
                    val words = chapterContent.split("\\s+".toRegex())
                    val wordChunks = words.chunked(400)
                    for (k in wordChunks.indices) {
                        if (wordsSearch.all { it in wordChunks[k] }) {
                            channel.send(Pair(CachingResults.bookList[i].id, j))
                            break
                        }
                    }
                }
            }
            jobs.add(job)
        }

        jobs.joinAll()
        channel.close()
        collectJob.join()
    }

    fun updateReaderProgress(bookId: String, chapterIndex: Int, chapterOffset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (CachingResults.currentUser.name.let { advancedSearchRepo.getReaderData(it, bookId) } != null) {
                val updates = mapOf(
                    "lastChapterIndex" to chapterIndex,
                    "lastChapterOffset" to chapterOffset
                )
                advancedSearchRepo.updateReaderData(CachingResults.currentUser.name, bookId, updates)
            } else {
                CachingResults.currentUser.name.let {
                    ReaderData(bookId, chapterIndex, chapterOffset, it)
                }.let { advancedSearchRepo.saveReaderData(readerData = it) }
            }
        }
    }
}