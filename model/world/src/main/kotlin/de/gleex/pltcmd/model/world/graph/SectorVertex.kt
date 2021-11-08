package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.Sector

class SectorVertex(val sector: Sector): CoordinateVertex(sector.origin) {

}
