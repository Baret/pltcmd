package de.gleex.pltcmd.options

import de.gleex.pltcmd.model.radio.AttenuationModel
import de.gleex.pltcmd.model.radio.PercentageReducingAttenuation
import de.gleex.pltcmd.model.world.Sector
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.data.Size3D

/**
 * Options that change the behaviour of the game.
 */
object GameOptions {
    val attenuationModel: Property<AttenuationModel> = createPropertyFrom(PercentageReducingAttenuation())

    /**
     * Option to turn on the RadioSignalVisualizer and corresponding UI elements.
     */
    val displayRadioSignals = createPropertyFrom(true)

    /**
     * Seed used to always create the same map which is useful for development.
     */
    const val DEBUG_MAP_SEED: Long = 5488540751658L

    const val SECTORS_COUNT_H = 10
    const val SECTORS_COUNT_V = 10
    /**
     * The size of the complete world in tiles as [Size3D].
     */
    val WORLD_SIZE: Size3D = Size3D.create(SECTORS_COUNT_H * Sector.TILE_COUNT, SECTORS_COUNT_V * Sector.TILE_COUNT, 1)
}