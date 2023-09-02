package de.gleex.pltcmd.game.application.graphics.elements

import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation

/**
 * A selector containing information for an element icon to be loaded.
 *
 * The three basic fields may be null so that the icon selection falls back to a default.
 *
 * Tags describe additional information for the icon, i.e. when it is for an anti-air element.
 */
data class ElementIconSelector(
    val affiliation: Affiliation? = null,
    val kind: ElementKind? = null,
    val size: Rung? = null,
    val tags: List<String> = emptyList()
)
