package de.gleex.pltcmd.util.graph.visualization

import com.mxgraph.layout.mxFastOrganicLayout
import com.mxgraph.layout.mxParallelEdgeLayout
import com.mxgraph.util.mxConstants
import de.gleex.pltcmd.util.debug.DebugFeature
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.ext.JGraphXAdapter
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
    fun <V : Any, E : Any> displayGraph(
        graph: Graph<V, E>,
        vertexLabelProvider: (V) -> String = { it.toString() },
        edgeLabelProvider: (E) -> String? = { null }
    ) {
        val adapter = buildAdapter(graph, vertexLabelProvider, edgeLabelProvider)
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
    }

    private fun <E : Any, V : Any> buildAdapter(
        graph: Graph<V, E>,
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
        adapter.isAutoOrigin = true
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
                edgeLabelProvider(edge),
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