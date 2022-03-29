package de.gleex.pltcmd.util.graph.visualization

import com.mxgraph.layout.mxFastOrganicLayout
import com.mxgraph.layout.mxParallelEdgeLayout
import com.mxgraph.util.mxConstants
import de.gleex.pltcmd.util.debug.DebugFeature
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.Graphs
import org.jgrapht.ListenableGraph
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.AbstractBaseGraph
import org.jgrapht.graph.DefaultListenableGraph
import org.jgrapht.graph.SimpleGraph
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.JButton
import javax.swing.JFrame

private val log = KotlinLogging.logger { }

/**
 * Used to have a quick way of visualizing a graph.
 */
@DebugFeature("to quickly draw a graph")
object GraphDisplayer {
    /**
     * Display a graph in a more or less reasonable layeouted manner.
     *
     * This method instantly opens a new window displaying the given graph.
     *
     * @param graph the graph that you would like to see.
     * @param vertexLabelProvider used to get the label to display for each vertex
     * @param edgeLabelProvider used to get the optional label for every edge
     */
    fun <V : Any, E : Any> displayGraph(
        graph: Graph<V, E>,
        vertexLabelProvider: (V) -> String = { it.toString() },
        edgeLabelProvider: (E) -> String? = { null }
    ) {
        val listenableGraph = listenableEmptyGraphOf(graph)
        val adapter = buildAdapter(listenableGraph, vertexLabelProvider, edgeLabelProvider)
        val component = com.mxgraph.swing.mxGraphComponent(adapter)
        layout(adapter)

        val frame = JFrame("${graph.vertexSet().size} vertices, ${graph.edgeSet().size} edges")
        frame.preferredSize = Toolkit.getDefaultToolkit().screenSize
        frame.extendedState = JFrame.MAXIMIZED_BOTH
        frame.contentPane.layout = BorderLayout()
        val layoutButton = JButton("Re-Layout")
        frame.contentPane.add(layoutButton, BorderLayout.NORTH)
        with(layoutButton) {
            toolTipText = "Run the layout again. May fix awkward looking graphs"
            preferredSize = Dimension(200, 30)
            this.addActionListener {
                layout(adapter)
            }
        }
        frame.contentPane.add(component, BorderLayout.CENTER)
        frame.maximizedBounds
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.pack()
        frame.isVisible = true
        runBlocking {
            graph.edgeSet().forEachIndexed { index, e ->
                Graphs.addEdgeWithVertices(listenableGraph, graph, e)
                if (index % 20 == 0) {
                    layout(adapter)
                }
            }
        }
    }

    private fun <V, E> listenableEmptyGraphOf(graph: Graph<V, E>): ListenableGraph<V, E> {
        if(graph is AbstractBaseGraph) {
            return DefaultListenableGraph(graph.clone() as Graph<V, E>)
        }
        val vertexIterator = graph.vertexSet().iterator()
        val edgeIterator = graph.edgeSet().iterator()
        val simple: SimpleGraph<V, E> = SimpleGraph({
            vertexIterator.next()
        }, {
            edgeIterator.next()
        }, graph.type.isWeighted)
        return DefaultListenableGraph(simple)
    }

    private fun <E : Any, V : Any> buildAdapter(
        graph: ListenableGraph<V, E>,
        vertexLabelProvider: (V) -> String,
        edgeLabelProvider: (E) -> String?
    ): JGraphXAdapter<V, E> {
        val adapter = JGraphXAdapter(graph)
        adapter.isAllowDanglingEdges = false
        adapter.isResetEdgesOnMove = true
        adapter.isResetViewOnRootChange = true
        adapter.isResetEdgesOnResize = true
        adapter.isCellsDeletable = false
        adapter.isCellsEditable = false
        adapter.isCellsMovable = true
        adapter.cellToVertexMap.forEach { (cell, vertex) ->
            adapter.cellLabelChanged(
                cell,
                vertexLabelProvider(vertex),
                true
            )
        }
        adapter.stylesheet.styles["REDEDGE"] = mapOf(mxConstants.STYLE_STROKECOLOR to "red")
        var redEdge = false
        adapter.cellToEdgeMap.forEach { (cell, edge) ->
            adapter.cellLabelChanged(
                cell,
                edgeLabelProvider(edge) ?: "${graph.getEdgeWeight(edge)}",
                true
            )
            if (redEdge) {
                adapter.model.setStyle(cell, "REDEDGE")
            }
            redEdge = !redEdge

        }
        return adapter
    }

    private fun <E : Any, V : Any> layout(adapter: JGraphXAdapter<V, E>) {
        with(mxFastOrganicLayout(adapter)) {
            forceConstant = 200.0
            isResetEdges = true
            initialTemp = 500.0
            execute(adapter.defaultParent)
        }

        with(mxParallelEdgeLayout(adapter, 100)) {
            execute(adapter.defaultParent)
        }
    }
}