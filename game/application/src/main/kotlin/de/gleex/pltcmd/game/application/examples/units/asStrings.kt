package de.gleex.pltcmd.game.application.examples.units

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.util.debug.DebugFeature

@DebugFeature("Just to see all FrontendStrings of the units")
fun main() {
    val formatsFormat = Format.entries
            .joinToString("", "", "|") { it.asFormatString() }
    val headerFormat = "| %3s $formatsFormat"
    val lineFormat = "| %3d $formatsFormat"

    val headerValues: MutableList<String> = Format.entries
            .map {
                it.toFrontendString(it).value
            }
            .toMutableList()
    headerValues.add(0, "Idx")
    val headerLine = headerFormat.format(*headerValues.toTypedArray())
    println(headerLine)
    repeat(headerLine.length) {
        print("=")
    }

    println()
    var i = 1
    Units.entries
            .groupBy { it.kind }
            .forEach { (kind, units) ->
                println(kind)
                units.forEach { unit ->
                    val formatValues: MutableList<Any> = Format.entries
                            .map {
                                unit.toFrontendString(it).value
                            }
                            .toMutableList()
                    formatValues.add(0, i++)
                    println(lineFormat.format(*formatValues.toTypedArray()))
                }
            }
    println()
    println("There you have all ${Units.entries.size} units.")
}

private fun Format.asFormatString() =
        if (this == Format.FULL) {
            "| %s "
        } else {
            "| %-${length}s "
        }