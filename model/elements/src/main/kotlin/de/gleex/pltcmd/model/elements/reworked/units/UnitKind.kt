package de.gleex.pltcmd.model.elements.reworked.units

/**
 * [Unit]s are of different kind. Foot soldiers belong to infantry, trucks are unarmored and main battle tanks are
 * heavily armored, for example.
 */
enum class UnitKind {
    Infantry,
    Unarmored,
    ArmoredLight,
    ArmoredHeavy,
    AerialLight,
    AerialHeavy
}
