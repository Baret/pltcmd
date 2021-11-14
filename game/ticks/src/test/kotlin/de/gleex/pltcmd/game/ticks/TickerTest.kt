package de.gleex.pltcmd.game.ticks

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TickerTest : StringSpec({

    "tick" {
        Ticker.jumpTo(TickId(0))

        Ticker.currentTick shouldBe TickId(0)
        Ticker.currentDay.value shouldBe 0
        Ticker.currentTimeString.value shouldBe Ticker.initialTime.toString()

        Ticker.tick()
        Ticker.currentTick shouldBe TickId(1)
        Ticker.currentDay.value shouldBe 0
        Ticker.currentTimeString.value shouldBe "23:51"

        repeat(3) { Ticker.tick() }
        Ticker.currentTick shouldBe TickId(4)
        Ticker.currentDay.value shouldBe 0
        Ticker.currentTimeString.value shouldBe "23:54"

        repeat(7) { Ticker.tick() }
        Ticker.currentTick shouldBe TickId(11)
        Ticker.currentDay.value shouldBe 1
        Ticker.currentTimeString.value shouldBe "00:01"
    }

    "jumpTo" {
        Ticker.jumpTo(TickId(0))
        Ticker.currentTick shouldBe TickId(0)
        Ticker.currentDay.value shouldBe 0
        Ticker.currentTimeString.value shouldBe Ticker.initialTime.toString()

        // forward
        Ticker.jumpTo(TickId(1337))
        Ticker.currentTick shouldBe TickId(1337)
        Ticker.currentDay.value shouldBe 1
        Ticker.currentTimeString.value shouldBe "22:07"

        // backward
        Ticker.jumpTo(TickId(42))
        Ticker.currentTick shouldBe TickId(42)
        Ticker.currentDay.value shouldBe 1
        Ticker.currentTimeString.value shouldBe "00:32"

        // a lot forward (more minutes than one year)
        Ticker.jumpTo(TickId(600_000))
        Ticker.currentTick shouldBe TickId(600_000)
        Ticker.currentDay.value shouldBe 417
        Ticker.currentTimeString.value shouldBe "15:50"

    }
})
