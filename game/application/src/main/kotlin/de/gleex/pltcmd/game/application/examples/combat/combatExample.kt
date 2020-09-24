package de.gleex.pltcmd.game.application.examples.combat

import de.gleex.pltcmd.game.application.Main
import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.toEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.screen.Screen

fun main() {
    CombatMain().run()
}

class CombatMain : Main() {
    /** demo map */
    override fun generateMap(screen: Screen, tileGrid: TileGrid, doneCallback: (WorldMap) -> Unit) {
        val generatedMap = DemoMap.create()
        doneCallback(generatedMap)
    }

    override fun createElementsToCommand(visibleSector: Sector, game: Game, gameWorld: GameWorld): List<ElementEntity> {
        val enemy1 = Elements.rifleSquad.new()
                .apply { callSign = CallSign("Alpha") }
        val alpha = visibleSector.createFriendly(enemy1, game, gameWorld, getBattlefield(game.world))
        return listOf(alpha)
    }

    override fun addHostiles(game: Game, gameWorld: GameWorld) {
        val worldMap = game.world
        val position = getBattlefield(worldMap).movedBy(1, 0)
        addHostile(position, worldMap)
                .let {
                    it.element.callSign = CallSign("Enemy rifle squad")
                    game.addEntity(it)
                    gameWorld.trackUnit(it)
                }

    }

    private fun getBattlefield(generatedMap: WorldMap) =
            generatedMap.sectors.first().origin.movedBy(20, 30)

    fun addHostile(position: Coordinate, map: WorldMap, element: CommandingElement = Elements.rifleSquad.new()): ElementEntity {
        val affiliation = Affiliation.Hostile
        val elementPosition = position.toProperty()
        val radioSender = RadioSender(elementPosition, GameOptions.defaultRadioPower, map)
        return element.toEntity(elementPosition, affiliation, radioSender)
    }
}
