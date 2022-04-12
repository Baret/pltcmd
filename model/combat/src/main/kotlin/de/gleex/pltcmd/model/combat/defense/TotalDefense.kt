package de.gleex.pltcmd.model.combat.defense

/**
 * Contains all factors that affect the defense (positive and negative).
 */
class TotalDefense(private vararg val factors: DefenseFactor) {
    val attackReduction: Double
        get() = factors.sumOf { it.attackReduction }.coerceIn(0.0, 1.0)

    override fun toString(): String {
        return "TotalDefense[attackReduction=$attackReduction from ${factors.contentToString()}]"
    }
}
