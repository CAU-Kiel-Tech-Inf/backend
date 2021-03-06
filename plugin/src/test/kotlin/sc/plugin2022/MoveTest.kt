package sc.plugin2022

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.helpers.shouldSerializeTo
import sc.protocol.room.RoomPacket

class MoveTest: FunSpec({
    val move = Move(Coordinates(0, 7), Coordinates(17, 5))
    test("$move is invalid") {
        move.isValid.shouldBeFalse()
        Move.create(move.from, move.delta).shouldBeNull()
    }
    context("Move manipulation") {
        test("reversal should not be equal") {
            move.reversed() shouldNotBe move
            move.reversed().compareTo(move) shouldBe 0
        }
        test("double reversal should yield identity") {
            move.reversed().reversed() shouldBe move
        }
    }
    test("Move XML") {
        RoomPacket("hi", move) shouldSerializeTo """
                <room roomId="hi">
                  <data class="move">
                    <from x="0" y="7"/>
                    <to x="17" y="5"/>
                  </data>
                </room>
            """.trimIndent()
        Move.create(0 y 1, Vector(1, 1))!! shouldSerializeTo """
                <move>
                  <from x="0" y="1"/>
                  <to x="1" y="2"/>
                </move>
            """.trimIndent()
    }
})