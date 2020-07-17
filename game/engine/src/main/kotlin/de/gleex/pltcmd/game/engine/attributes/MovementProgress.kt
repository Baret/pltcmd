package de.gleex.pltcmd.game.engine.attributes

import org.hexworks.amethyst.api.Attribute

/**
 * The movement progress holds the status of when an entity changes its tile.
 *
 * An entity might move slower that "one tile per minute (aka tick)". So we keep
 * its progress to know when it reaches the next tile.
 */
internal class MovementProgress(var progressInPercent: Double = 0.0): Attribute