package de.gleex.pltcmd.game.application.korge.elements.icons

import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation

data class IconSelector(
    val affiliation: Affiliation? = null,
    val kind: ElementKind? = null,
    val size: Rung? = null,
    val tags: List<String> = emptyList()
) {
    val fileName: String by lazy {
        "${affiliation.fileNamePart()}_${kind.fileNamePart()}_${size.fileNamePart()}${tags.fileNameParts()}"
    }
    
    private fun Affiliation?.fileNamePart() = when(this) {
        Affiliation.Unknown  -> "unk"
        Affiliation.Self     -> "fnd"
        Affiliation.Friendly -> "fnd"
        Affiliation.Neutral  -> "neu"
        Affiliation.Hostile  -> "hst"
        null                 -> "unk"
    }
    
    private fun ElementKind?.fileNamePart() = when(this) {
        ElementKind.Infantry           -> "inf"
        ElementKind.MotorizedInfantry  -> "mot"
        ElementKind.MechanizedInfantry -> "mec"
        ElementKind.Armored            -> "arm"
        ElementKind.Aerial             -> "air"
        null                           -> "knd"
    }
    
    private fun Rung?.fileNamePart() = when(this) {
        Rung.Fireteam  -> "siz"
        Rung.Squad     -> "siz"
        Rung.Platoon   -> "siz"
        Rung.Company   -> "siz"
        Rung.Battalion -> "siz"
        null           -> "siz"
    }

    private fun List<String>.fileNameParts() = joinToString { "_$it" }
}
