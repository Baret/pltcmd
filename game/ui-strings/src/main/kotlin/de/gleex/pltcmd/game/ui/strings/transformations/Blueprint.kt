package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.elements.blueprint.AbstractElementBlueprint

internal val blueprintTransformation: Transformation<AbstractElementBlueprint<*>> = { format ->
    // TODO: Implement actual transformation
    defaultTransformation(
        Elements.nameOf(this)
            ?.replace("(.)([A-Z])".toRegex(), "$1 $2")
            ?.capitalize()
            ?: "$corps $kind $rung",
        format)
}