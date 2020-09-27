package de.gleex.pltcmd.model.combat.defense

/** Describes in what condition a unit is in respect of fighting. It determines how well it is able to fight enemies. **/
enum class UnitFightingState(val availableForCombat: Boolean) {
    /** immediate operational readiness **/
    IOR(true),
    /** wounded in action **/
    WIA(false),
    /** killed in action **/
    KIA(false),
// not used in game
//    /** missing in action **/
//    MIA(null),
//    /** prisoner of war **/
//    POW(false),
}