package de.gleex.pltcmd.game.ui.fragments

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
                val themeResources = ColorThemeResource.values()
                addFragment(Fragments.
                        selector(contentSize.width, themeResources.toList()).
                        withDefaultSelected(themeResources.first { it.getTheme() == screen.theme }).
                        withCenteredText(true).
                        build()
                        .apply {
                            selectedValue.onChange { screen.theme = it.newValue.getTheme() }
                        })
            }
}
