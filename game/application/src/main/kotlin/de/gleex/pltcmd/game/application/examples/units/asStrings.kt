package de.gleex.pltcmd.game.application.examples.units

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.elements.units.Units

fun main() {
    val formatsFormat = Format.values()
            .joinToString("", "", "|") { it.asFormatString() }
    val headerFormat = "| %3s " + formatsFormat
    val lineFormat = "| %3d " + formatsFormat

    val headerValues: MutableList<String> = Format.values()
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
    Units.values()
            .groupBy { it.kind }
            .forEach { (kind, units) ->
                println(kind)
                units.forEach { unit ->
                    val formatValues: MutableList<Any> = Format.values()
                            .map {
                                unit.toFrontendString(it).value
                            }
                            .toMutableList()
                    formatValues.add(0, i++)
                    println(lineFormat.format(*formatValues.toTypedArray()))
                }
            }
    println()
    println("There you have all ${Units.values().size} units.")
}

private fun Format.asFormatString() =
        if (this == Format.FULL) {
            "| %s "
        } else {
            "| %-${length}s "
        }