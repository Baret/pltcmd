package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.options.UiOptions
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.internal.resource.BuiltInCP437TilesetResource

class TilesetSelectorFragment(override val width: Int, vararg components: Component) : BaseFragment {

    private val tilesets: List<BuiltInCP437TilesetResource> = BuiltInCP437TilesetResource.values().filter {
                                it.width == UiOptions.DEFAULT_TILESET.width && it.height == UiOptions.DEFAULT_TILESET.height
                            }

    override val root = Components.panel().
            withSize(width, 3).
            withDecorations(ComponentDecorations.box(BoxType.TOP_BOTTOM_DOUBLE, "Tileset")).
            build().
            apply {
                addFragment(Fragments.
                    multiSelect(contentSize.width, tilesets).
                    withDefaultSelected(tilesets.first { it.id == UiOptions.DEFAULT_TILESET.id }).
                    withCallback { _, newTileset -> components.forEach { it.tilesetProperty.value = newTileset } }.
                    withToStringMethod { it.tilesetName }.
                    build())
            }
}
