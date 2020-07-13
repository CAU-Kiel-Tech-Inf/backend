package sc.plugin2020

import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.framework.plugins.Player
import sc.shared.Team

class CloneTest: StringSpec({
    "clone Player" {
        val player = Player(Team.ONE, "aPlayer")
        val clone = player.clone()
        clone shouldBe player
        clone shouldNotBeSameInstanceAs player
    }
    "clone Board" {
        val board = Board()
        board.getField(0, 0, 0).pieces.add(Piece(Team.ONE, PieceType.BEETLE))
        val clone = board.clone()
        clone shouldBe board
        clone shouldNotBeSameInstanceAs board
        clone.getField(0, 0, 0) shouldNotBeSameInstanceAs board.getField(0, 0, 0)
        // note that the individual pieces are immutable and don't need to be cloned, only the stack which holds them
        clone.getField(0, 0, 0).pieces shouldNotBeSameInstanceAs board.getField(0, 0, 0).pieces
    }
    "clone GameState" {
        val state = GameState(blue = Player(Team.TWO, "aBluePlayer"), turn = 5)
        val clone = state.clone()
        clone shouldBe state
        clone shouldNotBeSameInstanceAs state
        clone.getDeployedPieces(Team.ONE) shouldBe state.getDeployedPieces(Team.ONE)
        clone.currentPlayerColor shouldBe state.currentPlayerColor
        clone.currentPlayer shouldNotBeSameInstanceAs state.currentPlayer
        clone.lastMove shouldBe state.lastMove
        clone.board shouldBe state.board
    }
    "clone another Game state" {
        val state = Game().gameState
        state.turn++
        state.currentPlayerColor shouldBe Team.TWO
        val clone = GameState(state)
        clone shouldBe state
        clone.turn shouldBe 1
        clone.currentPlayerColor shouldBe state.currentPlayerColor
    }
})