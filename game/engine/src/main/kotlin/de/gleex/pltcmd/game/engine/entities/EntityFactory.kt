package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.*
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.attributes.movement.MovementBaseSpeed
import de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.engine.entities.types.FOBEntity
import de.gleex.pltcmd.game.engine.entities.types.FOBType
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
import de.gleex.pltcmd.game.engine.systems.behaviours.*
import de.gleex.pltcmd.game.engine.systems.facets.*
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.signals.vision.builder.visionAt
import de.gleex.pltcmd.model.signals.vision.initialVision
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.extensions.FacetWithContext
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.amethyst.api.system.Behavior
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property

/**
 * The factory to create all entities.
 */
object EntityFactory {

    /**
     * Creates a new base (aka FOB = Forward operating base)
     */
    fun newBaseAt(position: Coordinate, map: WorldMap, callSign: CallSign = CallSign("HQ")): FOBEntity {
        return newEntityOfType(FOBType) {
            val positionProperty = position.toProperty()
            attributes(
                PositionAttribute(positionProperty),
                RadioAttribute(RadioCommunicator(callSign, RadioSender(positionProperty, RadioPower.STATIONARY, map))),
                VisionAttribute(map.visionAt(position, VisionPower(40.0)))
            )
            behaviors(
                Communicating,
                LookingAround
            )
            facets(
                ConversationSender,
                Detects
            )
        }
    }

    /**
     * Creates a new entity representing a [CommandingElement] in the game world.
     */
    fun newElement(
            element: CommandingElement,
            initialPosition: Property<Coordinate>,
            affiliation: Affiliation = Affiliation.Unknown,
            radioSender: RadioSender
    ): ElementEntity {
        val visualRange = if(element.kind == ElementKind.Aerial) {
            VisionPower(25.0)
        } else {
            VisionPower(10.0)
        }
        val attributes: MutableList<Attribute> = mutableListOf(
                CommandersIntent(),
                ElementAttribute(element, affiliation),
                PositionAttribute(initialPosition),
                VisionAttribute(initialVision(visualRange)),
                // TODO if call sign of the element gets mutable, use a function or ObservableValue as parameter (#98)
                RadioAttribute(RadioCommunicator(element.callSign, radioSender)),
                ShootersAttribute(element),

                MovementPath(),
                MovementBaseSpeed(element),
                MovementProgress()
        )
        // TODO: Make systems comparable so we do not need to make sure this if/else madness has the correct order
        // Lets say we have a speed limit for aerial elements (just for testing)
        if(element.kind == ElementKind.Aerial) {
            attributes += MovementModifier.SpeedCap(18.0)
        }

        val behaviors: MutableList<Behavior<GameContext>> = mutableListOf(
                IntentPursuing,
                LookingAround,
                MovingForOneMinute,
                Communicating,
                Fighting
        )
        if(element.kind == ElementKind.Infantry) {
            behaviors.add(0, StopsWhileTransmitting)
        }

        val facets: MutableList<FacetWithContext<GameContext>> = mutableListOf(
                Detects,
                PathFinding,
                ExecuteOrder,
                ConversationSender,
                PositionChanging
        )
        if(element.kind == ElementKind.Infantry) {
            facets.add(0, MakesSecurityHalts)
        }
        return newEntityOfType(ElementType) {
            attributes(*attributes.toTypedArray())
            behaviors(*behaviors.toTypedArray())
            facets(*facets.toTypedArray())
        }
    }

    fun newWanderingElement(element: CommandingElement, initialPosition: Property<Coordinate>, affiliation: Affiliation = Affiliation.Unknown, radioSender: RadioSender): ElementEntity =
            newElement(element, initialPosition, affiliation, radioSender)
                    .apply { addIfMissing(Wandering) }

}

/**
 * Turns this [CommandingElement] into an entity using [EntityFactory.newElement].
 */
fun CommandingElement.toEntity(elementPosition: Property<Coordinate>, affiliation: Affiliation, radioSender: RadioSender): ElementEntity {
    return EntityFactory.newElement(this, elementPosition, affiliation, radioSender)
}