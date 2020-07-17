package de.gleex.pltcmd.game.engine.attributes

import org.hexworks.amethyst.api.Attribute

/**
 * The base speed of a traveling element.
 */
data class MovementSpeed(val baseSpeedInKph: Double) : Attribute
