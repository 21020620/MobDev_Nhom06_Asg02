package com.example.mobdev2.repo

import org.koin.core.annotation.Single

@Single
class TestLibraryRepo: LibraryRepo {
    override fun removeBookFromLibrary(bookID: String) {
        return
    }
}