package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisibleAreaAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.*

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
    suspend fun updateVisibleArea(entity: SeeingEntity, context: GameContext): Boolean {
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
        val visible = TreeSet<Coordinate>()
        visible.add(from)
        val storeVisible: (Coordinate) -> Boolean = { element: Coordinate ->
            visible.add(element) && from.distanceTo(element) <= MAX_RANGE
        }
        addVisibleNeighbors(from, world, storeVisible)
        return CoordinateArea(visible)
    }

    private fun addVisibleNeighbors(current: Coordinate, world: WorldMap, storeVisible: (Coordinate) -> Boolean) {
        world.neighborsOf(current)
                .forEach { neighbor ->
                    val isVisible = isVisible(neighbor, current, world)
                    log.trace("$neighbor is visible from $current? $isVisible")
                    if (isVisible && storeVisible(neighbor)) {
                        addVisibleNeighbors(neighbor, world, storeVisible)
                    }
                }
    }

    private fun isVisible(next: Coordinate, from: Coordinate, world: WorldMap): Boolean {
        val currentHeight = world[from].height
        val nextHeight = world[next].height
        return currentHeight >= nextHeight
    }

}