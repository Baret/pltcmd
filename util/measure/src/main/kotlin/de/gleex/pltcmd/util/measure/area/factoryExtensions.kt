package de.gleex.pltcmd.util.measure.area

val Int.squareMeters: Area
    get() = toDouble().squareMeters

val Double.squareMeters: Area
    get() = Area(this, AreaUnit.squareMeters)

val Int.squareKilometers: Area
    get() = toDouble().squareKilometers

val Double.squareKilometers: Area
    get() = Area(this, AreaUnit.squareKilometers)