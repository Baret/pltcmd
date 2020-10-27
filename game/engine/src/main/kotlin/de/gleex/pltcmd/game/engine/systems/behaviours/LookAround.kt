package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisibleAreaAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
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

    // implements only type checking
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        if (entity.type !is Seeing) {
            return false
        }
        @Suppress("UNCHECKED_CAST")
        return updateVisibleArea(entity as SeeingEntity, context)
    }

    suspend fun updateVisibleArea(entity: SeeingEntity, context: GameContext): Boolean {
        val currentPosition = entity.currentPosition
        if (currentPosition != entity.lookingFrom) {
            // position changed
            entity.lookingFrom = currentPosition
            entity.visibleTiles = lookAround(currentPosition, context.world)
        }
        return true
    }

    suspend fun lookAround(from: Coordinate, world: WorldMap): CoordinateArea {
        val visible = TreeSet<Coordinate>()
        visible.add(from)
        runBlocking {
            addVisibleNeighbors(from, world).collect { visible.add(it) }
        }
        return CoordinateArea(visible)
    }

    private fun addVisibleNeighbors(current: Coordinate, world: WorldMap) = flow {
        world.neighborsOf(current)
                .forEach { neighbor ->
                    if (isVisible(neighbor, current)) {
                        emit(neighbor)
                    }
                }
    }

    private fun isVisible(next: Coordinate, from: Coordinate, world: WorldMap): Boolean {
        val currentHeight = world[from].height
        val nextHeight = world[next].height
        return currentHeight >= nextHeight
    }

}