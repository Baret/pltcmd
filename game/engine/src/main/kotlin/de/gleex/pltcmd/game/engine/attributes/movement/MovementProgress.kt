package de.gleex.pltcmd.game.engine.attributes.movement

import org.hexworks.amethyst.api.base.BaseAttribute

/**
 * The movement progress holds the status of when an entity changes its tile.
 *
 * An entity might move slower or faster than "one tile per tick". So we keep
 * its progress to know when it reaches the next tile.
 *
 * The progress is held as [tilesToAdvance] which is the number of tiles that need to be moved along in one tick.
 * It is increased with [plusAssign]. This should be done at the beginning of a tick after calculating how much
 * distance an entity travels in that time.
 *
 * While there is at least one full tile (see [hasTilesToAdvance]) to move the progress may be [advance]d to
 * simulate that an entity moved along 0 to n tiles in one tick according to its current speed.
 *
 * @param tilesToAdvance the current number of tiles that still needs to be advanced this tick.
 */
internal class MovementProgress(private var tilesToAdvance: Double = 0.0) : BaseAttribute() {
    /**
     * Adds the given number of tiles that need to be moved along.
     */
    operator fun plusAssign(travelDistanceInTiles: Double) {
        synchronized(this) {
            tilesToAdvance += travelDistanceInTiles
        }
    }

    /**
     * True when there are at least 1 full tile(s) that need to be [advance]d.
     */
    fun hasTilesToAdvance(): Boolean =
            synchronized(this) {
                tilesToAdvance >= 1.0
            }

    /**
     * Reduces the current progress by 1 tile if it [hasTilesToAdvance]
     */
    fun advance() {
        synchronized(this) {
            if (hasTilesToAdvance()) {
                tilesToAdvance -= 1.0
            }
        }
    }
}