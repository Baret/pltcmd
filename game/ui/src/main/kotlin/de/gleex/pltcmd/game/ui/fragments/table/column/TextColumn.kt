package de.gleex.pltcmd.game.ui.fragments.table.column

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.component.renderer.fillWithText
import org.hexworks.zircon.api.graphics.TileGraphics

/**
 * This [Column] creates a [Label] with a frontend string obtained from [componentCreator].
 */
open class TextColumn<T : Any, V : Any>(
    name: String,
    format: Format,
    foregroundColorProvider: (T) -> TileColor? = { null },
    backgroundColorProvider: (T) -> TileColor? = { null },
    valueAccessor: (T) -> V
) : Column<T, Label>(name, format, { rowElement: T ->
    Components
        .label()
        .withPreferredSize(format.length, 1)
        .withComponentRenderer(object : ComponentRenderer<Label> {
            override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<Label>) {
                var newStyle = context.currentStyle
                foregroundColorProvider(rowElement)
                    ?.let { fg -> newStyle = newStyle.withForegroundColor(fg) }
                backgroundColorProvider(rowElement)
                    ?.let { bg -> newStyle = newStyle.withBackgroundColor(bg) }
                tileGraphics.fillWithText(context.component.text, newStyle)
            }
        })
        .build()
        .apply { withFrontendString(format, valueAccessor(rowElement)) }
})