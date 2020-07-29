package sc.plugin2021

import com.thoughtworks.xstream.XStream
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import sc.plugin2021.util.Configuration
import sc.plugin2021.util.GameRuleLogic
import sc.shared.InvalidMoveException

// TODO: add more extensive tests with different GameStates
class GameStateTest: StringSpec({
    "GameState starts correctly" {
        val gameState = GameState()
        
        gameState.board shouldBe Board()
        
        gameState.undeployedPieceShapes[Color.BLUE]   shouldBe (0 until 21).toSet()
        gameState.undeployedPieceShapes[Color.YELLOW] shouldBe (0 until 21).toSet()
        gameState.undeployedPieceShapes[Color.RED]    shouldBe (0 until 21).toSet()
        gameState.undeployedPieceShapes[Color.GREEN]  shouldBe (0 until 21).toSet()
        gameState.undeployedPieceShapes[Color.NONE]   shouldBe emptySet<Int>()
    
        gameState.deployedPieces[Color.BLUE]   shouldBe mutableListOf<Piece>()
        gameState.deployedPieces[Color.YELLOW] shouldBe mutableListOf<Piece>()
        gameState.deployedPieces[Color.RED]    shouldBe mutableListOf<Piece>()
        gameState.deployedPieces[Color.GREEN]  shouldBe mutableListOf<Piece>()
        gameState.deployedPieces[Color.NONE]   shouldBe emptyList<Piece>()
     
        // TODO: adjust values accordingly
        gameState.getPointsForPlayer(Team.ONE)  shouldBe -178 // Twice the lowest score, once per color
        gameState.getPointsForPlayer(Team.TWO)  shouldBe -178
        gameState.getPointsForPlayer(Team.NONE) shouldBe 0
    }
    "GameStates know currently active Color" {
        var colorIter = Color.RED
        val gameState = GameState(startColor = colorIter)
        
        for (x in 0 until 4) {
            gameState.orderedColors[x] shouldBe colorIter
            colorIter = colorIter.next
        }
    
        gameState.currentColor = Color.RED
        gameState.turn++
        gameState.currentColor = Color.GREEN
        gameState.turn = 2
        gameState.currentColor = Color.YELLOW
    }
    "Pieces can only be placed once" {
        val gameState = GameState()
        val move = SetMove(
                Piece(Color.BLUE, 14, Rotation.RIGHT, true))
        
        gameState.undeployedPieceShapes[Color.BLUE]!!.size shouldBe 21
        gameState.deployedPieces[Color.BLUE]!!.size shouldBe 0
        assertDoesNotThrow {
            GameRuleLogic.performMove(gameState, move)
        }
        gameState.undeployedPieceShapes[Color.BLUE]!!.size shouldBe 20
        gameState.deployedPieces[Color.BLUE]!!.size shouldBe 1
        gameState.deployedPieces[Color.BLUE]!![0] shouldBe move.piece
        
        gameState.turn = 4
        assertThrows<InvalidMoveException> {
            GameRuleLogic.performMove(gameState, move)
        }
        gameState.undeployedPieceShapes[Color.BLUE]!!.size shouldBe 20
        gameState.deployedPieces[Color.BLUE]!!.size shouldBe 1
        gameState.deployedPieces[Color.BLUE]!![0] shouldBe move.piece
        
    }
    "XML conversion works" {
        val xstream = Configuration.xStream
        val state = GameState()
    
        xstream.fromXML(xstream.toXML(state)).toString() shouldBe state.toString()
        xstream.fromXML(xstream.toXML(state))            shouldBe state
    }
})