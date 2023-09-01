package de.gleex.pltcmd.game.application.graphics.elements

import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation

/**
 * A elector containing information for an element icon to be loaded.
 */
data class ElementIconSelector(
    val affiliation: Affiliation? = null,
    val kind: ElementKind? = null,
    val size: Rung? = null,
    val tags: List<String> = emptyList()
)
