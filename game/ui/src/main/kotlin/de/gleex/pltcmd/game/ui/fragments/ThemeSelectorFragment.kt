package de.gleex.pltcmd.game.ui.fragments

import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.internal.resource.ColorThemeResource

class ThemeSelectorFragment(override val fragmentWidth: Int, private val screen: Screen) : BaseFragment {
    override val root = Components.
            panel().
            withSize(fragmentWidth, 3).
            withDecorations(ComponentDecorations.box(BoxType.TOP_BOTTOM_DOUBLE, "Color theme")).
            build().apply {
                val themeResources = ColorThemeResource.values()
                addFragment(Fragments.
                        multiSelect(contentSize.width, themeResources.toList()).
                        withDefaultSelected(themeResources.first { it.getTheme() == screen.theme }).
                        withCallback { _, newValue -> screen.theme = newValue.getTheme() }.
                        withCenteredText(true).
                        build())
            }
}
