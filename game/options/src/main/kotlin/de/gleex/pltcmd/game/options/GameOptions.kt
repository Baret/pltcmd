package de.gleex.pltcmd.game.options

import de.gleex.pltcmd.model.world.Sector
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.data.Size3D

/**
 * Options that change the behaviour of the game.
 */
object GameOptions {

    /**
     * Option to turn on the RadioSignalVisualizer and corresponding UI elements.
     */
    val displayRadioSignals: Property<Boolean> = createPropertyFrom(true)

    /**
     * Seed used to always create the same map which is useful for development.
     */
    const val DEBUG_MAP_SEED: Long = 5488540751658L

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