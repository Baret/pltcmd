package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.combat.defense.TotalDefense
import org.hexworks.amethyst.api.base.BaseAttribute
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property

/**
 * Holds all values relevant for the combat defense.
 */
internal class DefenseAttribute() : BaseAttribute() {
    val total: Property<TotalDefense> = TotalDefense().toProperty()
}