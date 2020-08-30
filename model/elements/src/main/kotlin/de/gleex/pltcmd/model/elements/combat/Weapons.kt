package de.gleex.pltcmd.model.elements.combat

// some real data:
// - [Historical Probability of Hit (PHit) and Probability of Kill (PKill) Percentages](https://www.alternatewars.com/BBOW/Weapons/Historical_Phits_Pkills.htm)
// - [USMC Weapons Knowledge](http://www.bu.edu/nrotc/semperfi/gouge/Weapon[1].htm)
object Weapons {
    val none = Weapon(0, 0.0)
    val pistol = Weapon(8, 0.2)
    val assaultRifle = Weapon(12, 0.5)
    val sniperRifle = Weapon(3, 0.9) // accuracy at 300 m range like an assault rifle
    val mmg = Weapon(20, 0.1) // accuracy for a single shot. A 6-9 burst has approx. 0.7 probability
    val rpg = Weapon(1, 0.52)

    // 105 mm L7 main gun, HEAT rounds
    val tankGun = Weapon(1, 0.69) // at 1000 m
}
