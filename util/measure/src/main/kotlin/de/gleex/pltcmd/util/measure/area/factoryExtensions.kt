package de.gleex.pltcmd.util.measure.area

val Int.squareMeters: Area
    get() = toDouble().squareMeters

val Double.squareMeters: Area
    get() = Area(AreaUnit.squareMeters.inSquareMeters(this))

val Int.squareKilometers: Area
    get() = toDouble().squareKilometers

val Double.squareKilometers: Area
    get() = Area(AreaUnit.squareKilometers.inSquareMeters(this))