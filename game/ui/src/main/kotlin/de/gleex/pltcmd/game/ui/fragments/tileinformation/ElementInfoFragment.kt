package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.affiliation
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
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.color.ANSITileColor

/**
 * This fragment displays information about element(s) on a specific tile.
 *
 * Note: This fragment is still WIP and may be improved at any time. When there is more/other information
 * needed or expected or the layout can be improved, go ahead and do so.
 */
class ElementInfoFragment(
        override val fragmentWidth: Int,
        observedTile: Property<Coordinate>,
        private val game: Game
) : TileInformationFragment(observedTile) {

    private val currentElement: ObservableValue<Maybe<ElementEntity>> = currentInfoTile.bindTransform {
        Maybe.ofNullable(
                game.elementsAt(it)
                        .firstOrNull())
    }

    private val callSign: FrontendString<String> = whenElementPresent { it.callsign.name }

    private val description: FrontendString<String> = whenElementPresent { "  ${it.element.description}" }

    override val root = Components.vbox()
            .withSize(fragmentWidth, 2)
            .build()
            .apply {
                addComponent(
                        Components.hbox()
                                .withSpacing(1)
                                .withSize(width, 1)
                                .build()
                                .apply {
                                    val labelText = "Element:"
                                    addComponents(
                                            Components.header()
                                                    .withSize(labelText.length, 1)
                                                    .withText(labelText)
                                                    .build(),
                                            Components.icon()
                                                    .withIcon(TileRepository.empty())
                                                    .build()
                                                    .apply {
                                                        iconProperty.updateFrom(currentElement.bindTransform {
                                                                it.map { element ->
                                                                    TileRepository.Elements.marker(element.element, element.affiliation).withBackgroundColor(ANSITileColor.BLACK)
                                                                }
                                                                        .orElse(TileRepository.empty())
                                                            },
                                                                updateWhenBound = false)
                                                    },
                                            Components.header()
                                                    .withSize(width - labelText.length - 3, 1)
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
                                }
                )
                addComponent(Components.label()
                        .withSize(width, 1)
                        .build()
                        .apply {
                            withFrontendString(description)
                        })
            }

    private fun whenElementPresent(transformation: (ElementEntity) -> String): FrontendString<String> {
        return currentElement.bindTransform {
            if(it.isPresent) {
                transformation(it.get())
            } else {
                ""
            }
        }.toFrontendString(Format.SIDEBAR)
    }
}
