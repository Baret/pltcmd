package de.gleex.pltcmd.game.options

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.data.Size3D
import java.util.concurrent.TimeUnit

/**
 * Options that change the behaviour of the game.
 */
object GameOptions {

    /**
     * Option to turn on the RadioSignalVisualizer and corresponding UI elements.
     */
    val displayRadioSignals: Property<Boolean> = createPropertyFrom(false)

    /** The strength of a radio if not specified. */
    const val defaultRadioPower = 5000.0

    /**
     * The amount of time between each tick.
     */
    object tickRate {
        val duration: Long = 3L
        val timeUnit: TimeUnit = TimeUnit.SECONDS
    }

    /**
     * Seed used to always create the same map which is useful for development.
     */
    const val DEBUG_MAP_SEED: Long = 5488540751658L

    const val USE_DEBUG_MAP_SEED: Boolean = true

    val MAP_SEED: Long =
            if (USE_DEBUG_MAP_SEED) {
                DEBUG_MAP_SEED
            } else {
                System.currentTimeMillis()
            }

    /**
     * The origin of the map. In other words the world map's bottom left corner is this coordinate.
     */
    val MAP_ORIGIN: Coordinate = Coordinate(150, 300)

    /**
     * Horizontal number of sectors in the world.
     */
    const val SECTORS_COUNT_H: Int = 10

    /**
     * Vertical number of sectors in the world.
     */
    const val SECTORS_COUNT_V: Int = 10

    /**
     * The size of the complete world in tiles as [Size3D].
     */
    val WORLD_SIZE: Size3D = Size3D.create(SECTORS_COUNT_H * Sector.TILE_COUNT, SECTORS_COUNT_V * Sector.TILE_COUNT, 1)
}