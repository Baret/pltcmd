package de.gleex.pltcmd.options

import de.gleex.pltcmd.model.radio.PercentageReducingAttenuation
import de.gleex.pltcmd.model.world.Sector
import org.hexworks.zircon.api.data.Size3D

/**
 * Options that change the behaviour of the game.
 */
object GameOptions {
    var attenuationModel = PercentageReducingAttenuation()

    private const val SECTORS_COUNT_H = 10
    private const val SECTORS_COUNT_V = 10
    /**
     * The size of the complete world in tiles as [Size3D].
     */
    val WORLD_SIZE: Size3D = Size3D.create(GameOptions.SECTORS_COUNT_H * Sector.TILE_COUNT, GameOptions.SECTORS_COUNT_V * Sector.TILE_COUNT, 1)
}