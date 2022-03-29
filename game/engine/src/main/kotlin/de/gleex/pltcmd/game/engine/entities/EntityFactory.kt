package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.*
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.attributes.movement.MovementBaseSpeed
import de.gleex.pltcmd.game.engine.attributes.movement.MovementModifier
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.addIfMissing
import de.gleex.pltcmd.game.engine.systems.behaviours.*
import de.gleex.pltcmd.game.engine.systems.behaviours.Communicating
import de.gleex.pltcmd.game.engine.systems.facets.*
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.RadioCommunicator
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.signals.vision.builder.visionAt
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.measure.distance.kilometers
import de.gleex.pltcmd.util.measure.speed.perHour
import mu.KotlinLogging
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.extensions.FacetWithContext
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.amethyst.api.system.Behavior
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property

private val log = KotlinLogging.logger {  }

/**
 * The factory to create all entities.
 */
object EntityFactory {

    /**
     * Creates a new base (aka FOB = Forward operating base)
     */
    fun newBaseAt(position: Coordinate, map: WorldMap, faction: Faction, callSign: CallSign): FOBEntity {
        log.debug { "Spawning new FOB at $position" }
        return newEntityOfType<FOBType, GameContext>(FOBType) {
            val positionProperty = position.toProperty()
            attributes(
                FactionAttribute(faction),
                PositionAttribute(positionProperty),
                RadioAttribute(RadioCommunicator(callSign, RadioSender(positionProperty, RadioPower.STATIONARY, map))),
                VisionAttribute(map.visionAt(position, VisionPower(40.0))),
                SightedAttribute(),
                CommandersIntent(),
                Memory(map)
                    .apply { knownWorld reveal map.sectorAt(position) }
            )
            behaviors(
                Communicating,
                LookingAround
            )
            facets(
                ConversationSender,
                Detects,
                ExecuteOrder
            )
        }.also {
            log.debug { "Created FOB at $position: $it" }
        }
    }

    /**
     * Creates a new entity representing a [CommandingElement] in the game world.
     */
    fun newElement(
        element: CommandingElement,
        initialPosition: Property<Coordinate>,
        faction: Faction,
        radioSender: RadioSender,
        world: WorldMap
    ): ElementEntity {
        log.debug { "Spawning new element at ${initialPosition.value} for faction $faction: $element" }
        val visualRange = if (element.kind == ElementKind.Aerial) {
            VisionPower(25.0)
        } else {
            VisionPower(10.0)
        }
        val visionAttribute = VisionAttribute(world.visionAt(initialPosition.value, visualRange))
        val attributes: MutableList<Attribute> = mutableListOf(
            CommandersIntent(),
            ElementAttribute(element),
            FactionAttribute(faction),
            PositionAttribute(initialPosition),
            visionAttribute,
            SightedAttribute(),
            // TODO if call sign of the element gets mutable, use a function or ObservableValue as parameter (#98)
            RadioAttribute(RadioCommunicator(element.callSign, radioSender)),
            ShootersAttribute(element),
            Memory(world)
                .apply { knownWorld reveal visionAttribute.vision.area },

            MovementPath(),
            MovementBaseSpeed(element),
            MovementProgress()
        )
        // TODO: Make systems comparable so we do not need to make sure this if/else madness has the correct order
        // Lets say we have a speed limit for aerial elements (just for testing)
        if (element.kind == ElementKind.Aerial) {
            attributes += MovementModifier.SpeedCap(18.kilometers.perHour)
        }

        val behaviors: MutableList<Behavior<GameContext>> = mutableListOf(
            IntentPursuing,
            LookingAround,
            MovingForOneMinute,
            Communicating,
            Fighting
        )
        if (element.kind == ElementKind.Infantry) {
            behaviors.add(0, StopsWhileTransmitting)
        }

        val facets: MutableList<FacetWithContext<GameContext>> = mutableListOf(
            Detects,
            PathFinding,
            ExecuteOrder,
            ConversationSender,
            PositionChanging,
            ReportContacts
        )
        if (element.kind == ElementKind.Infantry) {
            facets.add(0, MakesSecurityHalts)
        }
        return newEntityOfType<ElementType, GameContext>(ElementType) {
            attributes(*attributes.toTypedArray())
            behaviors(*behaviors.toTypedArray())
            facets(*facets.toTypedArray())
        }.also {
            log.debug { "Spawned ${it.element.description} for faction $faction at ${initialPosition.value}" }
        }
    }

    fun newWanderingElement(
        element: CommandingElement,
        initialPosition: Property<Coordinate>,
        faction: Faction,
        radioSender: RadioSender,
        world: WorldMap
    ): ElementEntity =
        newElement(element, initialPosition, faction, radioSender, world)
            .apply { addIfMissing(Wandering) }

}

/**
 * Turns this [CommandingElement] into an entity using [EntityFactory.newElement].
 */
fun CommandingElement.toEntity(
    elementPosition: Property<Coordinate>,
    faction: Faction,
    radioSender: RadioSender,
    world: WorldMap
): ElementEntity {
    return EntityFactory.newElement(this, elementPosition, faction, radioSender, world)
}

/**
 * Creates a new FOB at the given position.
 *
 * @param position a valid position inside this [WorldMap]
 * @param callSign the [CallSign] of this base, default "HQ" (a main base)
 */
fun WorldMap.newBaseAt(position: Coordinate, faction: Faction, callSign: CallSign = CallSign("HQ")) =
    EntityFactory.newBaseAt(position, this, faction, callSign)