package com.learning.readingadventure

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class DeviceSmokeTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    @Test fun learnFirstWordWithoutNetwork() {
        compose.waitUntil(10000) { compose.onAllNodesWithContentDescription("Learn a").fetchSemanticsNodes().isNotEmpty() || compose.onAllNodesWithContentDescription("Learn a, completed").fetchSemanticsNodes().isNotEmpty() }
        compose.onAllNodes(hasContentDescription("Learn a",substring=true))[0].performClick()
        compose.onNodeWithContentDescription("Letter a, tile 1").performClick()
        compose.onNodeWithContentDescription("Home for a, position 1").performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("Tap the missing word").fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasText("a") and hasClickAction()).performClick()
        compose.onNodeWithText("You grew a new word!").assertExists()
    }
}
