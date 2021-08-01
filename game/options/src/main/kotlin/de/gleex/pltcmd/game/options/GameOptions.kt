package de.gleex.pltcmd.game.options

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import java.util.concurrent.TimeUnit

/**
 * Options that change the behaviour of the game.
 */
object GameOptions {

    /** The name entity that is controlled by the player **/
    val commandersCallSign = "HQ"

    /**
     * Option to turn on the RadioSignalVisualizer and corresponding UI elements.
     */
    val displayRadioSignals: Property<Boolean> = false.toProperty()

    /**
     * The amount of time between each tick.
     */
    object TickRate {
        const val duration: Long = 1L
        val timeUnit: TimeUnit = TimeUnit.SECONDS
    }

    /**
     * Seed used to always create the same map which is useful for development.
     */
    private const val DEBUG_MAP_SEED: Long = 5488540751658L

    /**
     * Set to true to "enable dev mode" which always generates the same map.
     */
    private const val DEV_MODE: Boolean = false

    /**
     * Seed used to generate the map, usually the current timestamp.
     */
    val MAP_SEED: Long =
            if (DEV_MODE) {
                DEBUG_MAP_SEED
            } else {
                System.currentTimeMillis()
            }
    val MAP_FILE = if (DEV_MODE) "develop" else "last"

    /**
     * The origin of the map. In other words the world map's bottom left corner is this coordinate.
     */
    val MAP_ORIGIN: Coordinate = Coordinate(850, 800)

    /**
     * Horizontal number of sectors in the world.
     */
    const val SECTORS_COUNT_H: Int = 10

    /**
     * Vertical number of sectors in the world.
     */
    const val SECTORS_COUNT_V: Int = 10
}