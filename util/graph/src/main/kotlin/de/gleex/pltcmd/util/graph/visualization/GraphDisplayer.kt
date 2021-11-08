package de.gleex.pltcmd.util.graph.visualization

import de.gleex.pltcmd.util.debug.DebugFeature
import org.jgrapht.Graph
import org.jgrapht.ext.JGraphXAdapter
import java.awt.Dimension
import javax.swing.JFrame

@DebugFeature("to quickly draw a graph")
object GraphDisplayer {
    fun displayGraph(graph: Graph<*, *>) {
        val adapter = JGraphXAdapter(graph)
        val frame = JFrame()
        val component = com.mxgraph.swing.mxGraphComponent(adapter)
        val layout = com.mxgraph.layout.mxFastOrganicLayout(adapter)
        layout.forceConstant = 200.0
        layout.execute(adapter.defaultParent)
        frame.preferredSize = Dimension(800, 800)
        frame.contentPane.add(component)
        frame.title = "JGraphT Adapter to JGraphX Demo"
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}