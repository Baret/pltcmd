package de.gleex.pltcmd.model.mapgeneration.mapgenerators.intermediate

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.GenerationContext
import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import mu.KotlinLogging
import kotlin.random.Random

/**
 * An intermediate generator generates a part of the final world. It may be called before or after other
 * intermediate generators. And it might be called to generate only a part of the whole world.
 */
abstract class IntermediateGenerator {
    abstract val rand: Random
    abstract val context: GenerationContext

    abstract fun generateArea(area: CoordinateArea, mutableWorld: MutableWorld)

    private val log = KotlinLogging.logger {}

    val processedTiles = mutableSetOf<Coordinate>()
    private val frontier = mutableSetOf<Coordinate>()
    private val nextFrontier = mutableSetOf<Coordinate>()

    /**
     * A "frontier" is a set of coordinates that need to be processed. For each coordinate a calculation has to be done
     * (i.e. calculate the height or type at that location). This might add new coordinates to the next iteration
     * calling [addToNextFrontier] (i.e. "each neighbor has to be processed next"). This creates a new frontier after
     * all entries of the current one have been processed (they are automatically added to [processedTiles]).
     *
     * When no invocation of a frontier adds entries to the next one the function stops.
     *
     * After all coordinates of a frontier have been processed, [afterFrontier] (optional) is being called.
     *
     * If a coordinate has been processed might be checked with [isProcessed] and [isNotProcessed].
     */
    protected fun workFrontier(
            initialFrontier: Set<Coordinate>,
            atEachNode: (currentCoordinate: Coordinate) -> Unit,
            afterFrontier: () -> Unit = {}
    ) {
        frontier.clear()
        frontier.addAll(initialFrontier)
        log.debug("Starting to process frontier starting with ${frontier.size} tiles")
        var frontiersProcessed = 1
        while(frontier.isNotEmpty()) {
            nextFrontier.clear()
            frontier.forEach(atEachNode)
            afterFrontier.invoke()
            processedTiles.addAll(frontier)
            log.debug("Processed ${processedTiles.size} tiles after $frontiersProcessed iterations")
            frontiersProcessed++
            frontier.clear()
            frontier.addAll(nextFrontier)
        }
    }

    /**
     * Extension function to directly use [workFrontier]
     */
    protected fun Set<Coordinate>.work(atEachNode: (currentCoordinate: Coordinate) -> Unit,
                                       afterFrontier: () -> Unit = {}) = workFrontier(this, atEachNode, afterFrontier)

    protected fun Coordinate.addToNextFrontier() = nextFrontier.add(this)

    /**
     * Checks if [processedTiles] contains this coordinate.
     */
    protected fun Coordinate.isProcessed() = processedTiles.contains(this)

    /**
     * Checks if this coordinate is not yet contained in [processedTiles]
     */
    protected fun Coordinate.isNotProcessed() = this.isProcessed().not()
}
