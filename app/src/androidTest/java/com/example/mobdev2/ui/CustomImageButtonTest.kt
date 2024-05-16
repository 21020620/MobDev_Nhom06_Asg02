package com.example.mobdev2.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.CustomImageButton
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class CustomImageButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setupContext() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun show_ValidInformation_exist() {
        composeRule.setContent { 
            CustomImageButton(
                imageID = R.drawable.art_books,
                content = "Art Books",
                backgroundColor = 0xF8FFC673,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeRule.onNodeWithText("Art Books").assertExists()
        composeRule.onNodeWithContentDescription("Genre Image").assertExists()
    }
}