package de.gleex.pltcmd.game.engine.attributes.movement

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.base.BaseAttribute
import java.util.*

/**
 * The current path an element is moving along.
 */
internal class MovementPath(var path: Stack<Coordinate> = Stack()): BaseAttribute()