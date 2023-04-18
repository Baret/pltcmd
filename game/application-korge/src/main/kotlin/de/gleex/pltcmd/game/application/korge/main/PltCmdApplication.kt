package de.gleex.pltcmd.game.application.korge.main

import com.soywiz.korge.Korge
import de.gleex.pltcmd.game.application.korge.module.PltCmdModule

/**
 * The default main to be used to start the game "as usual" (as the player would).
 */
suspend fun main() = Korge(config = Korge.Config(module = PltCmdModule))