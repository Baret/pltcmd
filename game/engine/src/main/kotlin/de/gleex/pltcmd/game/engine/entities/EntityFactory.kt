package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.attributes.*
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.systems.behaviours.*
import de.gleex.pltcmd.game.engine.systems.facets.*
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.cobalt.databinding.api.property.Property

object EntityFactory {

    fun newElement(element: CommandingElement, initialPosition: Property<Coordinate>, affiliation: Affiliation = Affiliation.Unknown, radioSender: RadioSender): ElementEntity =
            newEntityOfType(ElementType, {
                attributes(
                        CommandersIntent(),
                        ElementAttribute(element, affiliation),
                        PositionAttribute(initialPosition),
                        DestinationAttribute(),
                        // TODO if call sign of the element gets mutable, use a function or ObservableValue as parameter
                        RadioAttribute(RadioCommunicator(element.callSign, radioSender)),
                        CombatAttribute()
                )
                behaviors(IntentPursuing, Moving, Communicating, Fighting)
                facets(SetDestination, ExecuteOrder, ConversationSender)
            })

    fun newWanderingElement(element: CommandingElement, initialPosition: Property<Coordinate>, affiliation: Affiliation = Affiliation.Unknown, radioSender: RadioSender): ElementEntity =
            newElement(element, initialPosition, affiliation, radioSender).apply { asMutableEntity().addBehavior(Wandering) }

}

fun CommandingElement.toEntityAt(elementPosition: Property<Coordinate>, affiliation: Affiliation, radioSender: RadioSender): ElementEntity =
        newEntityOfType(ElementType) {
            attributes(
                    CommandersIntent(),
                    ElementAttribute(this@toEntityAt, affiliation),
                    PositionAttribute(elementPosition),
                    MovementPath(),
                    MovementSpeed(6.0),
                    MovementProgress(),
                    RadioAttribute(RadioCommunicator(callSign, radioSender)),
                    CombatAttribute()
            )
            behaviors(
                    Communicating,
                    MovingForOneMinute
            )
            facets(
                    ConversationSender,
                    ExecuteOrder,
                    PathFinding,
                    PositionChanging
            )
        }