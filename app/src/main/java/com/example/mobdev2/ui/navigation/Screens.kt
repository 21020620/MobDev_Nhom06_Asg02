package com.example.mobdev2.ui.navigation

sealed class Screens (val route: String) {
    data object Login: Screens("login")
    data object Signup: Screens("sign_up")
    data object PasswordReset: Screens("password_reset")
    data object ChooseBookGenres: Screens("choose_book_genres")
    data object BookDetail: Screens("book_detail")
}