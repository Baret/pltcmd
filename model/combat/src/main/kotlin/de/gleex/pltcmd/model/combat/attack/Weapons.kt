package de.gleex.pltcmd.model.combat.attack

// some real data:
// - [Historical Probability of Hit (PHit) and Probability of Kill (PKill) Percentages](https://www.alternatewars.com/BBOW/Weapons/Historical_Phits_Pkills.htm)
// - [USMC Weapons Knowledge](http://www.bu.edu/nrotc/semperfi/gouge/Weapon[1].htm)
// - [Equipment of the United States Army](https://military.wikia.org/wiki/Equipment_of_the_United_States_Army)
object Weapons {
    val none = WeaponStats(0, 0.0)
    val pistol = WeaponStats(8, 0.2)
    val assaultRifle = WeaponStats(12, 0.5)
    val sniperRifle = WeaponStats(3, 0.9) // accuracy at 300 m range like an assault rifle

    // 7.62x51mm
    val mmg = WeaponStats(21, 0.1) // accuracy for a single shot. A 6-9 burst has approx. 0.7 probability
    // .50 cal
    val hmg = WeaponStats(25, 0.15)

    val rpg = WeaponStats(1, 0.52)

    // 105 mm L7 main gun, HEAT rounds
    val tankGun = WeaponStats(1, 0.69) // at 1000 m
}
