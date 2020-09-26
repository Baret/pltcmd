package de.gleex.pltcmd.game.ui.strings.extensions

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.game.ui.strings.transformations.coordinateTransformation
import de.gleex.pltcmd.game.ui.strings.transformations.defaultTransformation
import de.gleex.pltcmd.game.ui.strings.transformations.unitTransformation
import de.gleex.pltcmd.game.ui.strings.transformations.unitsTransformation
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Creates a [FrontendString] of this observable value.
 */
fun <T: Any> ObservableValue<T>.toFrontendString(format: Format = Format.FULL): FrontendString<T> {
    return FrontendString(
            this,
            format,
            transformationFor(value)
    )
}

/**
 * Creates a [FrontendString] for any object not wrapped into an [ObservableValue].
 *
 * @see ObservableValue.toFrontendString
 */
fun <T : Any> T.toFrontendString(format: Format = Format.FULL): FrontendString<T> =
        this.toProperty().toFrontendString(format)

@Suppress("UNCHECKED_CAST")
private fun <T> transformationFor(value: T): Transformation<T> {
    return when (value) {
        is Coordinate -> coordinateTransformation
        is Units      -> unitsTransformation
        is Unit       -> unitTransformation
        else          -> defaultTransformation
    } as Transformation<T>
}