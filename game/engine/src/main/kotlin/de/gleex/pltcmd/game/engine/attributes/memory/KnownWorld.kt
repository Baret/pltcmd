package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.FilteredCoordinateArea
import de.gleex.pltcmd.util.knowledge.Known

/**
 * Knowledge about the [WorldMap]. It is initialized as completely unrevealed
 * and gets revealed over time.
 */
class KnownWorld(world: WorldMap) : Known<WorldMap, KnownWorld> {

    override val origin: WorldMap = world

    private val knowTerrainMap = mutableMapOf<Coordinate, KnownTerrain>()

    /**
     * @return the [KnownTerrain] at the given location.
     */
    operator fun get(coordinate: Coordinate): KnownTerrain {
        return knowTerrainMap.getOrPut(coordinate) {
            if(coordinate in origin) {
                KnownTerrain(origin[coordinate], isRevealed = false)
            } else {
                WorldTile(coordinate.eastingFromLeft, coordinate.northingFromBottom).unrevealed()
            }
        }
    }

    /**
     * Reveals the given [Coordinate].
     */
    infix fun reveal(toReveal: Coordinate) {
        get(toReveal).reveal()
    }

    /**
     * Reveals the complete [WorldArea].
     */
    infix fun reveal(areaToReveal: CoordinateArea) {
        areaToReveal.asSequence().forEach { reveal(it) }
    }

    override fun mergeWith(other: KnownWorld): Boolean {
        val knowPreviously = knowTerrainMap.size
        knowTerrainMap.putAll(other.knowTerrainMap.filter { it.value.revealed })
        return knowPreviously != knowTerrainMap.size
    }

    /**
     * Gets all unknown tiles in the given [CoordinateArea].
     *
     * All returned [Coordinate]s are not revealed.
     */
    fun getUnknownIn(area: CoordinateArea): CoordinateArea =
        FilteredCoordinateArea(area) { get(it).revealed.not() }

}