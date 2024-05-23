package com.example.mobdev2.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobdev2.TestNavigator
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.destinations.ReaderReviewsScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewBookScreenTest {
    @get: Rule
    val composeRule = createComposeRule()

    private lateinit var mockNavigator: DestinationsNavigator
    private val timeoutMillis = 10000L

    @Before
    fun setUp() {
        val bookID = "The Da Vinci Code"
        composeRule.setContent {
            val navController = rememberNavController()
            mockNavigator = TestNavigator(navController)
            DestinationsNavHost(navGraph = NavGraphs.bookGraph, navController = navController)
            navController.navigate(ReaderReviewsScreenDestination(bookID).route)
        }
    }

    @Test
    fun assert_ReaderReviews_canSeeReviewList() {
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText("tuỵttt").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("tuỵttt").assertIsDisplayed()
    }

    @Test
    fun assert_ReaderReviews_canReviewBook() {

        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis) {
            try {
                composeRule.onNodeWithTag("ReviewButton").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeRule.onNodeWithTag("ReviewButton").performClick()

        composeRule.waitUntil(timeoutMillis) {
            try {
                composeRule.onNodeWithTag("ReviewTextField").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        val reviewText = "This is a test review."
        composeRule.onNodeWithTag("ReviewTextField").performTextInput(reviewText)

        composeRule.onNodeWithText("Submit").performClick()
    }


    @Test
    fun assert_ReaderReviews_cannotReviewBook() {
        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis) {
            try {
                composeRule.onNodeWithTag("ReviewButton").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeRule.onNodeWithTag("ReviewButton").performClick()

        composeRule.waitUntil(timeoutMillis) {
            try {
                composeRule.onNodeWithTag("ReviewTextField").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        val prohibitedReviewText = "This book is racist."
        composeRule.onNodeWithTag("ReviewTextField").performTextInput(prohibitedReviewText)

        composeRule.onNodeWithText("Submit").performClick()

        composeRule.waitForIdle()

        composeRule.onNodeWithText("Warning").assertIsDisplayed()
        composeRule.onNodeWithText("Your review contains prohibited words:").assertIsDisplayed()
        composeRule.onNodeWithText("- racist").assertIsDisplayed()
    }

    @Test
    fun assert_ReaderReviews_canReply() {
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText("Reply").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithText("Reply")[0].performClick()

        val replyText = "This is a test reply."
        composeRule.onNodeWithText("Type your reply here...").performTextInput(replyText)
        composeRule.onNodeWithText("Submit").performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(replyText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(replyText).assertIsDisplayed()
    }

    @Test
    fun assert_ReaderReviews_cannotReply() {
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithText("Reply").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithText("Reply")[0].performClick()

        val prohibitedReplyText = "This reply is racist."
        composeRule.onNodeWithText("Type your reply here...").performTextInput(prohibitedReplyText)
        composeRule.onNodeWithText("Submit").performClick()

        composeRule.onNodeWithText("Warning").assertIsDisplayed()
        composeRule.onNodeWithText("Your reply contains prohibited words:").assertIsDisplayed()
        composeRule.onNodeWithText("- racist").assertIsDisplayed()
    }
}