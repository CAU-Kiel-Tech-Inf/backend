package sc.plugin2021

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldNotBe
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.shared.PlayerScore
import sc.shared.ScoreCause

class GameTest: FreeSpec({
    "A few moves can be performed without issues" {
        val game = Game()
        val state = game.gameState
        game.onPlayerJoined().color shouldBe Team.ONE
        game.onPlayerJoined().color shouldBe Team.TWO
        game.start()

        while (true) {
            try {
                val condition = game.checkWinCondition()
                if (condition != null) {
                    println(condition)
                    break
                }
                val moves = GameRuleLogic.getPossibleMoves(state)
                moves shouldNotBe emptySet<SetMove>()
                game.onAction(state.currentPlayer, moves.random())
            } catch (e: Exception) {
                println(e.message)
                break
            }
        }
    }
    "A game of skips eventually ends in a draw" {
        val game = Game()
        val state = game.gameState
        Pair(game.onPlayerJoined(), game.onPlayerJoined())
        game.start()

        for (s in 0 until 4)
            game.onAction(state.currentPlayer, GameRuleLogic.streamPossibleMoves(state).first())
        
        shouldNotThrowAny {
            while (!game.checkGameOver()) {
                game.onAction(state.currentPlayer, SkipMove(state.currentColor))
            }
        }
        shouldThrow<java.lang.IndexOutOfBoundsException> {
            game.onAction(state.currentPlayer, SkipMove(state.currentColor))
        }
    
        game.playerScores shouldContainExactly List(2) {
            PlayerScore(ScoreCause.REGULAR, "", Constants.DRAW_SCORE, 10)
        }
    }
})
