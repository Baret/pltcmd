package de.gleex.pltcmd.model.faction

/**
 * The affiliation of an element describes how a faction sees it. This may differ for the same element seen by different factions.
 */
enum class Affiliation {
    Unknown,
    Self,
    Friendly,
    Neutral,
    Hostile
}