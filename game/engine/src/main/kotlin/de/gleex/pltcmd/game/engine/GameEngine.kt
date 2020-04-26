package de.gleex.pltcmd.game.engine

import org.hexworks.amethyst.api.Engine

/** The engine used by the game (implementation of Amethyst's [Engine] **/
class GameEngine : Engine<GameContext> by Engine.default()