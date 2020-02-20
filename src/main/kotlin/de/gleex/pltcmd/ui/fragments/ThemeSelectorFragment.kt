package de.gleex.pltcmd.ui.fragments

import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.internal.resource.ColorThemeResource

class ThemeSelectorFragment(override val width: Int, private val screen: Screen) : BaseFragment {
    override val root = Components.
            panel().
            withSize(width, 3).
            withDecorations(ComponentDecorations.box(BoxType.TOP_BOTTOM_DOUBLE, "Color theme")).
            build().apply {
                addFragment(Fragments.
                        multiSelect(contentSize.width, ColorThemeResource.values().toList()).
                        // TODO: Set default value to current theme with the next zircon version
                        withCallback { _, newValue -> screen.theme = newValue.getTheme() }.
                        withCenteredText(true).
                        build())
            }
}
