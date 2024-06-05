package de.gleex.pltcmd.game.application.examples.elements

import de.gleex.pltcmd.model.elements.Elements

fun main() {
    val numberOfInstances = 20

    println("lets create some elements to see their callsigns...")
    println()
    Elements.allCommandingElements().forEach { (name, blueprint) ->
        println("Generating $numberOfInstances instances of $name")
        repeat(numberOfInstances) {
            println("\t${blueprint.new().callSign}")
        }
    }
}