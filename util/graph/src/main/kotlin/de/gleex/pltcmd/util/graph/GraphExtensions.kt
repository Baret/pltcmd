package de.gleex.pltcmd.util.graph

import org.jgrapht.Graph
import org.jgrapht.GraphTests

// This file contains extensions to the JGraphT API to ease it's usage in kotlin.

/**
 * Calls [Graph.getEdge].
 */
operator fun <V : Any, E : Any> Graph<V, E>.get(v1: V?, v2: V?): E? = getEdge(v1, v2)

/**
 * [GraphTests.isConnected] as extension function.
 */
fun AnyGraph.isConnected() = GraphTests.isConnected(this)