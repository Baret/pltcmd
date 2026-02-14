package de.gleex.pltcmd.game.application.editor

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import de.gleex.pltcmd.util.measure.distance.times

/**
 * The world's origin (bottom left) [Coordinate].
 */
internal val worldOrigin: Coordinate = Coordinate.zero

/**
 * How many tiles the world has from west to east.
 */
internal const val worldWidthInTiles: Int = 150

/**
 * How many tiles the world has from south to north.
 */
internal const val worldHeightInTiles: Int = 150

/**
 * The edge length of a single tile as [Distance]. Tiles are square.
 */
internal val worldTileEdgeLength: Distance = WorldTile.edgeLength

/**
 * The edge length of a single tile as [Float] in meters. Tiles are square.
 */
internal val worldTileEdgeLengthInMeters: Float = worldTileEdgeLength.inUnit(DistanceUnit.Meters).toFloat()

/**
 * The number of tiles that make up the edge of a [Sector] as [Distance]. Sectors are square.
 */
internal val worldSectorEdgeLength: Distance = Sector.TILE_COUNT * worldTileEdgeLength

/**
 * The number of tiles that make up the edge of a [Sector] as [Float] in meters. Sectors are square.
 */
internal val worldSectorEdgeLengthInMeters: Float = worldSectorEdgeLength.inUnit(DistanceUnit.Meters).toFloat()