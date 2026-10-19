package com.learning.readingadventure

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class GameLogicTest {
    @Test fun matchingRejectsWrongLetterAndOutOfBounds() {
        val board = LetterBoard("cat")
        assertEquals(board, board.drop(0,1))
        assertEquals(board, board.drop(-1,0))
        assertEquals(board, board.drop(0,9))
        assertEquals(mapOf(0 to 0), board.drop(0,0).assignments)
    }
    @Test fun repeatedLettersCanUseEitherMatchingSlot() {
        var board = LetterBoard("little")
        board = board.drop(3,2).drop(2,3)
        assertEquals(mapOf(3 to 2, 2 to 3), board.assignments)
        assertEquals(board, board.drop(3,3))
        assertEquals(board, board.drop(0,2))
        for (index in listOf(0,1,4,5)) board = board.drop(index,index)
        assertTrue(board.complete)
    }
    @Test fun allPermutationsOfRepeatedLettersRemainSolvable() {
        for(text in listOf("little","there","green","yellow","all")) {
            var board=LetterBoard(text)
            for(tile in text.indices.reversed()) {
                val target=text.indices.first { it !in board.assignments.values && text[it]==text[tile] }
                board=board.drop(tile,target)
            }
            assertTrue(board.complete)
        }
    }
    @Test fun progressionRequiresDistinctWordsInAppropriateLevels() {
        val words=(1..3).flatMap { level -> (1..15).map { n -> LearningWord("$level-$n","cat",level,"cat","A cat sleeps.") } }
        assertEquals(1, Progression.unlockedLevel(emptySet(),words))
        val first=(1..12).map { "1-$it" }.toSet()
        assertEquals(2,Progression.unlockedLevel(first,words))
        assertEquals(2,Progression.unlockedLevel(first+(1..11).map{"2-$it"},words))
        assertEquals(3,Progression.unlockedLevel(first+(1..12).map{"2-$it"},words))
        assertEquals(1,Progression.unlockedLevel((1..15).map{"3-$it"}.toSet(),words))
    }
    @Test fun staleAndCancelledSpeechCannotCompleteCurrentAnimation() {
        val tokens=AudioTokens(); val first=tokens.begin(); val second=tokens.begin()
        assertFalse(tokens.complete(first)); assertTrue(tokens.speaking)
        assertTrue(tokens.complete(second)); assertFalse(tokens.speaking)
        assertFalse(tokens.complete(second))
        val third=tokens.begin(); tokens.cancel(); assertFalse(tokens.complete(third))
    }
    @Test fun motionClampsAndSettles() {
        assertEquals(0f,Motion.snap(-1f),0f); assertEquals(1f,Motion.snap(2f),0f)
        assertTrue(Motion.snap(.5f)>.5f)
        assertEquals(0f,Motion.celebration(1f),0f)
        for(t in 0..100) assertTrue(kotlin.math.abs(Motion.bounce(t/10f,2f))<=2f)
    }
    @Test fun contentHasAtLeast100ValidatedOriginalEntries() {
        val words=Content.parse(File("src/main/assets/content/core.json").readText())
        assertEquals(108,words.size)
        assertEquals(108,words.map{it.text}.distinct().size)
        for(level in 1..3) assertTrue(words.count{it.difficulty==level}>=12)
        assertTrue(words.all{it.animation.bounce>0f})
    }
    @Test(expected=IllegalArgumentException::class) fun badContentSchemaRejected() { Content.parse("{\"schemaVersion\":2,\"words\":[]}") }
    @Test fun letterSoundModeIsExplicit() {
        assertEquals("B",LetterSounds.forLetter('b',false))
        assertEquals("buh",LetterSounds.forLetter('b',true))
    }
}
