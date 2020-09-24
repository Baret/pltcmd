package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.combat.Weapon
import org.hexworks.amethyst.api.Attribute

/** The offensive part of the given element. */
internal class ShootersAttribute(weapons: Iterable<Weapon>) : Attribute {
    constructor(element: Element) : this(element.allUnits.map { it.blueprint.weapon })

    // remember "half shots" for if a shot is done after a longer time period than given in a single call
    // Note: Do not use a Map as the same Weapon object will be used multiple times!
    internal val shooters: List<Pair<Weapon, PartialShot>> = weapons.map { Pair(it, PartialShot(0.0)) }
}

class PartialShot(var value: Double)
