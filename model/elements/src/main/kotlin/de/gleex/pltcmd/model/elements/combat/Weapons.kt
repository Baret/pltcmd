package de.gleex.pltcmd.model.elements.combat

// some real data: [Historical Probability of Hit (PHit) and Probability of Kill (PKill) Percentages](https://www.alternatewars.com/BBOW/Weapons/Historical_Phits_Pkills.htm)

val pistol = Weapon(8, 0.2)
val assaultRifle = Weapon(12, 0.5)
val sniperRifle = Weapon(3, 0.9) // accuracy at 300 m range
val mmg = Weapon(20, 0.7)
val rpg = Weapon(1, 0.52)
// 105 mm L7 main gun, HEAT rounds
val tankGun = Weapon(1, 0.69) // at 1000 m
