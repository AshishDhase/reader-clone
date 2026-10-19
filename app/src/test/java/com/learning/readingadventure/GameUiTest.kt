package com.learning.readingadventure

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[35])
class GameUiTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    @Test fun wordCanBeCompletedAndGardenReached() {
        compose.waitUntil(10000) { compose.onAllNodesWithContentDescription("Learn a").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithContentDescription("Learn a").performClick()
        compose.onNodeWithContentDescription("Letter a, tile 1").performClick()
        compose.onNodeWithContentDescription("Home for a, position 1").performClick()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(500))
        compose.waitUntil(5000) { compose.onAllNodesWithText("Tap the missing word").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Tap the missing word").assertExists()
        compose.onNode(hasText("a") and hasClickAction()).performClick()
        compose.onNodeWithText("You grew a new word!").assertExists()
        compose.onNodeWithText("Back to the garden").performClick()
        compose.onNodeWithContentDescription("Learn a, completed").assertExists()
    }
    @Test fun settingsRequireParentGate() {
        compose.onNodeWithText("Parents").performClick()
        compose.onNodeWithText("Grown-ups only").assertExists()
        compose.onNodeWithText("Open settings").performClick()
        compose.onNodeWithText("Try again").assertExists()
        compose.onNodeWithText("Back to play").performClick()
        compose.onNodeWithText("Grown-ups only").assertDoesNotExist()
    }
}
