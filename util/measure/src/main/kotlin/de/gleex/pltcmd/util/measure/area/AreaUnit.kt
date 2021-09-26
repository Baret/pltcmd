package de.gleex.pltcmd.util.measure.area

enum class AreaUnit(internal val factorToSquareMeters: Int) {
    squareMeters(1),
    squareKilometers(1_000_000);

    fun inSquareMeters(amount: Double): Double = amount * factorToSquareMeters
}