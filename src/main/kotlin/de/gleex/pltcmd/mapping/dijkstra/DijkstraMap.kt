package de.gleex.pltcmd.mapping.dijkstra

import org.hexworks.cobalt.datatypes.Maybe

/**
 * A generic and rather simple implementation of a DijkstraMap (http://www.roguebasin.com/index.php?title=Dijkstra_Maps_Visualized)
 * It must be built node by node, which is handy when a map generator sets one tile after another and wants to
 * provide a way of tracing a path back to the origin.
 *
 */
class DijkstraMap<T>(vararg initialTargets: T) {
    private val _targets = initialTargets.toMutableSet()
    val targets
        get() = _targets.toSet()


    private val downstreamMap = mutableMapOf<T, Pair<T, Int>>()

    val maxDistance: Int
        get() {
            return downstreamMap.map { it.value.second }.max() ?: 0
        }

    fun addTarget(target: T) {
        _targets.add(target)
    }

    fun add(from: T, to: T, targetDistance: Int) {
        downstreamMap[from] = Pair(to, targetDistance)
    }

    fun pathFrom(from: T): Maybe<Sequence<T>> {
        if(_targets.contains(from)) {
            return Maybe.of(sequenceOf(from))
        }
        if(downstreamMap.containsKey(from)) {
            return Maybe.of(sequence {
                val path = mutableListOf<T>(from)
                var current = downstreamMap[from]?.first
                do {
                    current?.let { path.add(it) }
                    if(downstreamMap[current]?.second == 0) {
                        // last node, next would be a target
                        path.add(downstreamMap[current]?.first!!)
                    }
                    val nextNode = downstreamMap[current]?.first
                    current = nextNode
                } while (current != null && downstreamMap.containsKey(current))
                yieldAll(path)
            })
        }
        return Maybe.empty()
    }

    fun distanceFrom(from: T): Maybe<Int> {
        if(_targets.contains(from)) {
            return Maybe.of(0)
        }
        val distance = downstreamMap[from]?.let {
            it.second + 1
        }
        return Maybe.ofNullable(distance)
    }

    /**
     * Finds all nodes with the given distance to a target.
     */
    fun allWithDistance(distance: Int): Set<T> = downstreamMap.
                                                    filter { (_, value) -> (value.second + 1) == distance}.
                                                    keys
}