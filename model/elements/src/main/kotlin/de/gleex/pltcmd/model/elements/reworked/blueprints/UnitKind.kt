package de.gleex.pltcmd.model.elements.reworked.blueprints

/**
 * Units are of different kind. Foot soldiers belong to infantry, trucks are unarmored and main battle tanks are
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
