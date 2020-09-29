package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.game.ui.strings.extensions.withFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.Components
import kotlin.time.ExperimentalTime

class ElementInfoFragment(
        override val width: Int,
        observedTile: Property<Coordinate>,
        private val game: Game
) : TileInformationFragment(observedTile) {

    private val currentElement: Property<Maybe<ElementEntity>> = Maybe.empty<ElementEntity>().toProperty()

    override val root = Components.vbox()
            .withSize(width, 2)
            .build()
            .apply {
                addComponent(
                        Components.header()
                                .withSize(width, 1)
                                .build()
                                .apply {
                                    val elementString = currentElement.toFrontendString(Format.SIDEBAR)
                                            .bindTransform { if (currentElement.value.isPresent) it else "none" }
                                    withFrontendString(Format.SIDEBAR, "Element: ", elementString)
                                })
            }

    @ExperimentalTime
    override fun updateInformation(newCoordinate: Coordinate) {
        currentElement.updateValue(
                Maybe.ofNullable(
                        game.elementsAt(newCoordinate).firstOrNull()))
    }
}