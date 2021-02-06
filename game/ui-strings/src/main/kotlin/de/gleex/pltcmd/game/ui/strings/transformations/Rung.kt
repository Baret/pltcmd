package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.elements.Rung

internal val rungTransformation: Transformation<Rung> = { format ->
    when(format) {
        Format.ICON    -> {
            when(this) {
                Rung.Fireteam  -> "F"
                Rung.Squad     -> "S"
                Rung.Platoon   -> "P"
                Rung.Company   -> "C"
                Rung.Battalion -> "B"
            }
        }
        Format.SHORT3  -> {
            when(this) {
                Rung.Fireteam  -> "FT"
                Rung.Squad     -> "SQD"
                Rung.Platoon   -> "PLT"
                Rung.Company   -> "CPY"
                Rung.Battalion -> "BTL"
            }
        }
        Format.SHORT5  -> {
            when(this) {
                Rung.Fireteam  -> "FTeam"
                Rung.Squad     -> "Squad"
                Rung.Platoon   -> "Pltn"
                Rung.Company   -> "Copny"
                Rung.Battalion -> "Batln"
            }
        }
        Format.SIDEBAR, Format.FULL -> {
            when(this) {
                Rung.Fireteam  -> "Fireteam"
                Rung.Squad     -> "Squad"
                Rung.Platoon   -> "Platoon"
                Rung.Company   -> "Company"
                Rung.Battalion -> "Battalion"
            }
        }
    }
}
