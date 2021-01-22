package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.elements.Corps

internal val corpsTransformation: Transformation<Corps> = { format ->
    when(format) {
        Format.ICON    -> {
            when(this) {
                Corps.Fighting       -> "F"
                Corps.Logistics      -> "L"
                Corps.CombatSupport  -> "S"
                Corps.Reconnaissance -> "R"
            }
        }
        Format.SHORT3  -> {
            when(this) {
                Corps.Fighting       -> "FIG"
                Corps.Logistics      -> "LOG"
                Corps.CombatSupport  -> "SUP"
                Corps.Reconnaissance -> "REC"
            }
        }
        Format.SHORT5  -> {
            when(this) {
                Corps.Fighting       -> "Fight"
                Corps.Logistics      -> "Logi"
                Corps.CombatSupport  -> "CSupp"
                Corps.Reconnaissance -> "Recon"
            }
        }
        Format.SIDEBAR, Format.FULL -> {
            when(this) {
                Corps.Fighting       -> "Fighting"
                Corps.Logistics      -> "Logistics"
                Corps.CombatSupport  -> "Combat support"
                Corps.Reconnaissance -> "Reconnaissance"
            }
        }
    }
}
