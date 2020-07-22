package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.*
import de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.attributes.movement.MovementSpeed
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.systems.behaviours.*
import de.gleex.pltcmd.game.engine.systems.facets.*
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.amethyst.api.system.Behavior
import org.hexworks.cobalt.databinding.api.property.Property

object EntityFactory {

    fun newElement(element: CommandingElement, initialPosition: Property<Coordinate>, affiliation: Affiliation = Affiliation.Unknown, radioSender: RadioSender): ElementEntity =
            newEntityOfType(ElementType, {
                attributes(
                        CommandersIntent(),
                        ElementAttribute(element, affiliation),
                        PositionAttribute(initialPosition),
                        // TODO if call sign of the element gets mutable, use a function or ObservableValue as parameter
                        RadioAttribute(RadioCommunicator(element.callSign, radioSender)),
                        CombatAttribute()
                )
                behaviors(IntentPursuing, Moving, Communicating, Fighting)
                facets(ExecuteOrder, ConversationSender)
            })

    fun newWanderingElement(element: CommandingElement, initialPosition: Property<Coordinate>, affiliation: Affiliation = Affiliation.Unknown, radioSender: RadioSender): ElementEntity =
            newElement(element, initialPosition, affiliation, radioSender).apply { asMutableEntity().addBehavior(Wandering) }

}

fun CommandingElement.toEntityAt(elementPosition: Property<Coordinate>, affiliation: Affiliation, radioSender: RadioSender): ElementEntity {
    val attributes: MutableList<Attribute> = mutableListOf(
            CommandersIntent(),
            ElementAttribute(this@toEntityAt, affiliation),
            PositionAttribute(elementPosition),
            RadioAttribute(RadioCommunicator(this@toEntityAt.callSign, radioSender)),
            CombatAttribute(),

            // Trying stuff...
            MovementPath(),
            MovementSpeed(this@toEntityAt),
            MovementProgress()
    )
    // Lets say we have a speed limit for aerial elements (just for testing)
    if(kind == ElementKind.Aerial) {
        attributes += MovementModifier.SpeedCap(18.0)
    }

    val behaviors: MutableList<Behavior<GameContext>> = mutableListOf(
            IntentPursuing,
            MovingForOneMinute,
            Communicating,
            Fighting
    )
    if(kind == ElementKind.Infantry) {
        behaviors.add(0, StopsWhileTransmitting)
    }
    return newEntityOfType(ElementType) {
        attributes(*attributes.toTypedArray())
        behaviors(*behaviors.toTypedArray())
        facets(PathFinding, ExecuteOrder, ConversationSender, PositionChanging)
    }
}