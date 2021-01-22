package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.elements.ElementKind

internal val elementKindTransformation: Transformation<ElementKind> = { format ->
    when(format) {
        Format.ICON    -> {
            when(this) {
                ElementKind.Infantry           -> "I"
                ElementKind.MotorizedInfantry  -> "."
                ElementKind.MechanizedInfantry -> "/"
                ElementKind.Armored            -> "A"
                ElementKind.Aerial             -> "^"
            }
        }
        Format.SHORT3  -> {
            when(this) {
                ElementKind.Infantry           -> "INF"
                ElementKind.MotorizedInfantry  -> "MOI"
                ElementKind.MechanizedInfantry -> "MEI"
                ElementKind.Armored            -> "ARM"
                ElementKind.Aerial             -> "AIR"
            }
        }
        Format.SHORT5  -> {
            when(this) {
                ElementKind.Infantry           -> "Inf."
                ElementKind.MotorizedInfantry  -> "MoInf"
                ElementKind.MechanizedInfantry -> "MeInf"
                ElementKind.Armored            -> "Armor"
                ElementKind.Aerial             -> "Air"
            }
        }
        Format.SIDEBAR, Format.FULL -> {
            when(this) {
                ElementKind.Infantry           -> "Infantry"
                ElementKind.MotorizedInfantry  -> "Motorized infantry"
                ElementKind.MechanizedInfantry -> "Mechanized infantry"
                ElementKind.Armored            -> "Armored"
                ElementKind.Aerial             -> "Aerial"
            }
        }
    }
}
