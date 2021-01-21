package de.gleex.pltcmd.game.ui.fragments.table

import de.gleex.pltcmd.game.ui.components.ElementsTable
import de.gleex.pltcmd.game.ui.fragments.BaseFragment
import de.gleex.pltcmd.game.ui.strings.Format
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.HBox
import org.hexworks.zircon.api.component.TextArea
import org.hexworks.zircon.api.data.Size

class TableFilterPanel(override val fragmentWidth: Int, private val columnConfig: Map<String, Format>): BaseFragment {
    override val root: HBox = Components
        .hbox()
        .withSpacing(ElementsTable.COLUMN_SPACING)
        .withSize(fragmentWidth, 1)
        .build()

    val size: Size = root.size

    private val nameFilterField: TextArea = Components
        .textArea()
        .withSize(columnConfig["Name"]!!.length - 1, 1)
        .withText("Filters are TBD...")
        .build()

    private val nameFilterClearButton: Button = Components
        .button()
        .withText("X")
        .withDecorations()
        .build()
        .apply {
            onActivated { nameFilterField.text = "" }
        }

    init {
        root.addComponent(Components.label().withText(" "))
        root.addComponent(
            Components
                .hbox()
                .withSpacing(0)
                .withSize(nameFilterField.size.withRelativeWidth(nameFilterClearButton.width))
                .build()
                .apply { addComponents(nameFilterField, nameFilterClearButton) }
        )
    }
}
