package com.example.mobdev2.ui.screens.book.main

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@NavGraph("book_graph")
annotation class BookNavGraph(
    val start: Boolean = false
)
