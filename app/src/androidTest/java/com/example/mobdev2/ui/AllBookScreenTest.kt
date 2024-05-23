package com.example.mobdev2.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
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
class AllBookScreenTest : KoinTest {
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
    fun assert_AllBookScreen_canSeeBookList() {
        composeRule.onNodeWithText("The Da Vinci Code").assertIsDisplayed()
        composeRule.onNodeWithText("The Da Vinci Code").performClick()
    }

    @Test
    fun assert_AllBookScreen_canSeeBookDetail1() {
        composeRule.onNodeWithText("The Da Vinci Code").assertIsDisplayed()
        composeRule.onNodeWithText("The Da Vinci Code").performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag("synopsis"), 5000L)
        composeRule.onNodeWithText("Synopsis").assertIsDisplayed()
        composeRule.onNodeWithText("This is a Fake Synopsis").assertIsDisplayed()
    }

    @Test
    fun assert_AllBookScreen_canSeeSimilarBook() {
        composeRule.onNodeWithText("The Da Vinci Code").assertIsDisplayed()
        composeRule.onNodeWithText("The Da Vinci Code").performClick()

        composeRule.waitUntilExactlyOneExists(hasTestTag("synopsis"), 5000L)
        composeRule.onNodeWithText("The Green Beret").assertIsDisplayed()
    }
}