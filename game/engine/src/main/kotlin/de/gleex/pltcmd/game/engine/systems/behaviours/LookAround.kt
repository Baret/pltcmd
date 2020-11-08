package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisibleAreaAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.coordinate.fillCircle
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Behavior of an entity that updates the [VisibleAreaAttribute] each tick. **/
object LookAround :
        BaseBehavior<GameContext>(
                PositionAttribute::class,
                VisibleAreaAttribute::class
        ) {

    private val log = LoggerFactory.getLogger(LookAround::class)
    private const val MAX_RANGE = 30

    // implements only type checking
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        if (entity.type !is Seeing) {
            return false
        }
        @Suppress("UNCHECKED_CAST")
        return updateVisibleArea(entity as SeeingEntity, context)
    }

    /**
     * Calculates and stores the visible area of the entity if necessary.
     */
    fun updateVisibleArea(entity: SeeingEntity, context: GameContext): Boolean {
        val currentPosition = entity.currentPosition
        if (currentPosition != entity.lookingFrom || entity.visibleTiles.isEmpty) {
            // position changed or view was reset
            val visibleTiles = lookAround(currentPosition, context.world)
            log.debug("Updating vision of ${(entity as ElementEntity).callsign} to $visibleTiles")
            entity.updateVision(currentPosition, visibleTiles)
        }
        return true
    }

    private fun lookAround(from: Coordinate, world: WorldMap): CoordinateArea {
        val visible = from.fillCircle(MAX_RANGE)
        visible.removeIf { !it.isVisibleFrom(from, world) }
        return CoordinateArea(visible)
    }

}

/** @return true if no higher terrain is on the direct line between the two tiles */
fun Coordinate.isVisibleFrom(from: Coordinate, world: WorldMap): Boolean {
    val fromHeight = world[from].height.value
    val targetHeight = world[this].height.value
    val deltaHeight = targetHeight - fromHeight
    val distance = from.distanceTo(this)
    val /* m = */ heightPerDistance = deltaHeight / distance
    // the function that describes the line between the two terrain heights and provides the height at each coordinate:
    // f(x) = m*x + b
    val lineOfSight: (Coordinate) -> Double = { location -> from.distanceTo(location) * heightPerDistance + /* b = */ fromHeight  }
    // stop when the terrain height blocks the direct line
    return CoordinatePath.line(from, this)
            .dropWhile { lineOfSight.invoke(it) >= world[it].height.value }
            .isEmpty()
}
