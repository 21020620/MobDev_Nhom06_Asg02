package com.example.mobdev2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobdev2.repo.AllBookRepo
import com.example.mobdev2.repo.TestAllBookRepo
import com.example.mobdev2.ui.screens.book.AllBookScreen
import com.example.mobdev2.ui.screens.book.AllBookViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class AllBookScreenTest : KoinTest {
    @get: Rule
    val composeRule = createComposeRule()

    @Test
    fun assert_AllBookScreen_haveBook() {
        val mockNavigator = mockk<DestinationsNavigator>()

        composeRule.setContent {
            AllBookScreen(navigator = mockNavigator)
        }

        composeRule.onNodeWithText("The Da Vinci Code").assertIsDisplayed()
    }
}