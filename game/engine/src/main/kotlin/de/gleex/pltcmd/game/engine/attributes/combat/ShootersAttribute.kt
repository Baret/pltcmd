package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.combat.attack.WeaponStats
import de.gleex.pltcmd.model.combat.attack.weapon
import de.gleex.pltcmd.model.elements.Element
import org.hexworks.amethyst.api.Attribute

/** The offensive part of the given element. */
internal class ShootersAttribute(weapons: Iterable<WeaponStats>) : Attribute {
    constructor(element: Element) : this(element.allUnits.map { it.blueprint.weapon })

    internal val shooters: List<Shooter> = weapons.map { Shooter(it) }
}

/** Remembers "half shots" if a shot is done after a longer time period than given in a single update of an entity. */
// Note: This is intentional not a data class as the same values are not an equal object, because every shooter is unique!
internal class Shooter(val weapon: WeaponStats, var partialShot: Double = 0.0) {
    operator fun component1(): WeaponStats = weapon
    operator fun component2(): Double = partialShot
}
