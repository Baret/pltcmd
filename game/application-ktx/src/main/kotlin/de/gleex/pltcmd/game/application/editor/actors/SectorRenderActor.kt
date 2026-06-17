package de.gleex.pltcmd.game.application.editor.actors

import com.badlogic.gdx.scenes.scene2d.Group
import de.gleex.pltcmd.game.application.editor.worldSectorEdgeLengthInMeters
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.coordinate.Coordinate

class SectorRenderActor(private val sectorOrigin: Coordinate, private val world: MutableWorld) : Group() {
    init {
        x = (sectorOrigin.eastingFromLeft - world.bottomLeftCoordinate.eastingFromLeft) * worldSectorEdgeLengthInMeters
        y =
            (sectorOrigin.northingFromBottom - world.bottomLeftCoordinate.northingFromBottom) * worldSectorEdgeLengthInMeters
        val size = Sector.TILE_COUNT * worldSectorEdgeLengthInMeters
        setSize(size, size)
        for (coordinate in sectorOrigin..sectorOrigin.movedBy(Sector.TILE_COUNT, Sector.TILE_COUNT)) {
            addActor(HeightRenderActor(coordinate).also { world.addListener(it) })
        }
    }
}