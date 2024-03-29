package de.gleex.pltcmd.util.graph


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
            return downstreamMap.map { it.value.second }.maxOrNull() ?: 0
        }

    fun addTarget(target: T) {
        _targets.add(target)
    }

    fun add(from: T, to: T, targetDistance: Int) {
        downstreamMap[from] = Pair(to, targetDistance)
    }

    fun pathFrom(from: T): List<T> {
        if(_targets.contains(from)) {
            return listOf(from)
        }
        if(downstreamMap.containsKey(from)) {
            val path = ArrayList<T>(maxDistance).apply { add(from) }
            var current = downstreamMap[from]?.first
            while (current != null && !path.contains(current)) {
                path.add(current)
                val nextNode = downstreamMap[current]?.first
                if(downstreamMap[current]?.second == 0 && nextNode != null) {
                    // last node, next would be a target
                    path.add(nextNode)
                }
                current = nextNode
            }
            return path
        }
        return emptyList()
    }

    fun distanceFrom(from: T): Int? {
        if(_targets.contains(from)) {
            return 0
        }
        val distance = downstreamMap[from]?.let {
            it.second + 1
        }
        return distance
    }

    /**
     * Finds all nodes with the given distance to a target.
     */
    fun allWithDistance(distance: Int): Set<T> = downstreamMap.
                                                    filter { (_, value) -> (value.second + 1) == distance}.
                                                    keys
}