package com.example.mobdev2.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobdev2.TestNavigator
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.BookHomeScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class LibraryScreenTest : KoinTest {
    @get: Rule
    val composeRule = createComposeRule()

    private lateinit var mockNavigator: DestinationsNavigator

    @Before
    fun setUp() {
        composeRule.setContent {
            val navController = rememberNavController()
            mockNavigator = TestNavigator(navController)
            DestinationsNavHost(navGraph = NavGraphs.bookGraph, navController = navController)
            navController.navigate(BookHomeScreenDestination.route)
        }
    }

    @Test
    fun assert_LibraryScreen_canAddBookToLib() {
        composeRule.onNodeWithText("The Da Vinci Code").assertIsDisplayed()
        composeRule.onNodeWithText("Operating System Concepts").performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag("synopsis"), 5000L)
        composeRule.onNodeWithText("Synopsis").assertIsDisplayed()
        composeRule.onNodeWithText("This is a Fake Synopsis").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Add book to lib").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Go back").performClick()
        composeRule.onNodeWithText("Library").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Delete").assertIsDisplayed()
    }

    @Test
    fun assert_LibraryScreen_canRemoveBookFromLib() {
        composeRule.onNodeWithText("The Da Vinci Code").assertIsDisplayed()
        composeRule.onNodeWithText("Operating System Concepts").performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag("synopsis"), 5000L)
        composeRule.onNodeWithText("Synopsis").assertIsDisplayed()
        composeRule.onNodeWithText("This is a Fake Synopsis").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Add book to lib").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Go back").performClick()
        composeRule.onNodeWithText("Library").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Delete").assertIsDisplayed()
        composeRule.onNodeWithText("Delete").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Operating Systems Concepts").assertIsNotDisplayed()
    }
}