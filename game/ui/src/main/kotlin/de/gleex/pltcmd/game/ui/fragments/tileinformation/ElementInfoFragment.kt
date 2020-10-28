package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.Components
import kotlin.time.ExperimentalTime

/**
 * This fragment displays information about element(s) on a specific tile.
 *
 * Note: This fragment is still WIP and may be improved at any time. When there is more/other information
 * needed or expected or the layout can be improved, go ahead and do so.
 */
class ElementInfoFragment(
        override val width: Int,
        observedTile: Property<Coordinate>,
        private val game: Game
) : TileInformationFragment(observedTile) {

    private val currentElement: Property<Maybe<ElementEntity>> = Maybe.empty<ElementEntity>().toProperty()

    private val callSign: FrontendString<String> = whenElementPresent { it.callsign.toString() }

    private val description: FrontendString<String> = whenElementPresent { "  ${it.element.description}" }

    override val root = Components.vbox()
            .withSize(width, 2)
            .build()
            .apply {
                addComponent(
                        Components.header()
                                .withSize(width, 1)
                                .build()
                                .apply {
                                    withFrontendString(Format.SIDEBAR,
                                            "Element: ",
                                            callSign.bindTransform { cs -> if(cs.isBlank()) { "none" } else { cs } })
                                })
                addComponent(Components.label()
                        .withSize(width, 1)
                        .build()
                        .apply {
                            withFrontendString(description)
                        })
            }

    @ExperimentalTime
    override fun updateInformation(newCoordinate: Coordinate) {
        currentElement.updateValue(
                Maybe.ofNullable(
                        game.elementsAt(newCoordinate).firstOrNull()))
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