package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.affiliationTo
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.color.ANSITileColor

/**
 * This fragment displays information about element(s) on a specific tile.
 *
 * Note: This fragment is still WIP and may be improved at any time. When there is more/other information
 * needed or expected or the layout can be improved, go ahead and do so.
 */
class ElementInfoFragment(
        observedTile: Property<Coordinate>,
        private val game: Game
) : InfoSidebarFragment(observedTile, title = "Element", neededHeight = 2) {

    // TODO: Handle multiple elements (list binding, show list of elements)
    private val currentElement: ObservableValue<ElementEntity?> = currentInfoTile.bindTransform {
            // TODO use knowledge of HQ
            game.elementsAt(it)
                .firstOrNull()
    }

    private val callSign: FrontendString<String> = whenElementPresent { it.callsign.name }

    private val description: FrontendString<String> = whenElementPresent { "  ${it.element.description}" }

    init {
        componentsContainer.addComponents(
                Components.hbox()
                        .withSpacing(1)
                        .withPreferredSize(SUB_COMPONENT_WIDTH, 1)
                        .build()
                        .apply {
                            addComponents(
                                    Components.icon()
                                            .withIcon(TileRepository.empty())
                                            .build()
                                        .apply {
                                            iconProperty.updateFrom(
                                                currentElement.bindTransform {
                                                    it?.let { element ->
                                                        TileRepository.Elements.marker(
                                                            element.element,
                                                            element.affiliationTo(game.playerFaction)
                                                        )
                                                            .withBackgroundColor(ANSITileColor.BLACK)
                                                    } ?: TileRepository.empty()
                                                },
                                                updateWhenBound = false
                                            )
                                        },
                                    Components.header()
                                            .withPreferredSize(width - 3, 1)
                                            .build()
                                            .apply {
                                                withFrontendString(Format.SIDEBAR,
                                                        callSign.bindTransform { cs ->
                                                            if (cs.isBlank()) {
                                                                "none"
                                                            } else {
                                                                cs
                                                            }
                                                        })
                                            }
                            )
                        },
                Components.label()
                        .withPreferredSize(SUB_COMPONENT_WIDTH, 1)
                        .build()
                        .apply {
                            withFrontendString(description)
                        }
        )
    }

    private fun whenElementPresent(transformation: (ElementEntity) -> String): FrontendString<String> {
        return currentElement.bindTransform {
            if(it != null) {
                transformation(it)
            } else {
                ""
            }
        }.toFrontendString(Format.SIDEBAR)
    }
}
