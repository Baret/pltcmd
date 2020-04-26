package de.gleex.pltcmd.game.engine

import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.internal.DefaultEngine

/** Currently just another name for [DefaultEngine] **/
class GameEngine : Engine<GameContext> by DefaultEngine()