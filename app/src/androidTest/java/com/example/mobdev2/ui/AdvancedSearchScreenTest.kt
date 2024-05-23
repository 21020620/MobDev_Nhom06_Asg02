package com.example.mobdev2.ui

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobdev2.CachingResults
import com.example.mobdev2.R
import com.example.mobdev2.TestNavigator
import com.example.mobdev2.repo.model.Book
import com.example.mobdev2.repo.model.Chapter
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.AdvancedSearchScreenDestination
import com.example.mobdev2.ui.screens.destinations.BookHomeScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class AdvancedSearchScreenTest : KoinTest {
    @get: Rule
    val composeRule = createComposeRule()

    private lateinit var mockNavigator: DestinationsNavigator

    @Before
    fun setUp() {
        composeRule.setContent {
            val navController = rememberNavController()
            mockNavigator = TestNavigator(navController)
            DestinationsNavHost(navGraph = NavGraphs.bookGraph, navController = navController)
            navController.navigate(AdvancedSearchScreenDestination.route)
            CachingResults.bookList = listOf(
                Book(
                "The Da Vinci Code",
                "The Da Vinci Code",
                "Dan Brown",
                listOf("Historical Fiction", "Mystery", "Thriller"),
                "English",
                "https://upload.wikimedia.org/wikipedia/en/thumb/6/6b/DaVinciCode.jpg/220px-DaVinciCode.jpg",
                listOf(Chapter("Chapter 01", "This is a Fake Chapter")),
                "This is a Fake Synopsis",
                listOf()
            ),
                Book(
                    "Operating System Concepts",
                    "Operating System Concepts",
                    "Teacher Thanh",
                    listOf("Non-fiction", "Educational", "Thriller"),
                    "English",
                    "https://upload.wikimedia.org/wikipedia/en/thumb/6/6b/DaVinciCode.jpg/220px-DaVinciCode.jpg",
                    listOf(Chapter("Chapter 01", "This is a Fake Chapter for Operating System Concepts")),
                    "This is a Fake Synopsis for OS Theory",
                    listOf()
                )
            )
        }
    }

    @Test
    fun assert_AdvancedSearchScreen_haveBasicComponents() {
        composeRule.onNodeWithText("Advanced Search").assertIsDisplayed()

        composeRule.onNodeWithTag("input").performTextInput("Fake")
        composeRule.onNodeWithContentDescription("SearchButton").performClick()
        composeRule.waitForIdle()

        composeRule.waitUntilAtLeastOneExists(hasText("The Da Vinci Code"), 5000L)
        composeRule.onNodeWithText("The Da Vinci Code").assertIsDisplayed()
    }
}