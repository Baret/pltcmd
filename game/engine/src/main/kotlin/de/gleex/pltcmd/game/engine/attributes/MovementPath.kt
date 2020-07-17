package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import java.util.*

/**
 * The current path an element is moving along.
 */
internal data class MovementPath(var path: Stack<Coordinate> = Stack()): Attribute