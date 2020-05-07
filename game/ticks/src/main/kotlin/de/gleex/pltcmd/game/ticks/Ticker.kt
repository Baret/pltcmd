package de.gleex.pltcmd.game.ticks

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.attributes.callsign
import de.gleex.pltcmd.game.engine.extensions.ElementEntity
import de.gleex.pltcmd.game.engine.systems.facets.MoveTo
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.events.globalEventBus
import kotlinx.coroutines.runBlocking
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * This singleton is responsible for publishing _ticks_ via the [EventBus] and by this advance the ingame time.
 */
object Ticker {

    private val log = LoggerFactory.getLogger(Ticker::class)

    /**
     * The current tick the simulation is currently in. Or "the last tick that happened".
     */
    val currentTick: TickId
        get() = _currentTickProperty.value

    /**
     * This tick will be the next one (i.e. the next [TickEvent] will contain this tick)
     */
    val nextTick get() = currentTick.next

    private val _currentTickProperty = createPropertyFrom(TickId(0))

    private val _currentTimeProperty: Property<LocalTime>
    private val _currentTimeStringProperty: Property<String>

    private val executor = Executors.newScheduledThreadPool(1)

    private lateinit var currentGame: Game

    init {
        val initialTime = LocalTime.of(5, 59)
        _currentTimeProperty = createPropertyFrom(initialTime)
        _currentTimeStringProperty = createPropertyFrom(initialTime.toString()).
                                        apply {
                                            updateFrom(_currentTimeProperty, false) { localTime -> localTime.toString()}
                                        }
    }

    /**
     * The current ingame time as [ObservableValue] so that it is possible to listen for changes.
     */
    val currentTime: ObservableValue<LocalTime> = _currentTimeProperty
    /**
     * The current ingame time converted to a string as [ObservableValue] so that it is possible to listen for changes.
     */
    val currentTimeString: ObservableValue<String> = _currentTimeStringProperty
    /**
     * The [currentTick] but observable.
     */
    val currentTickObservable: ObservableValue<TickId> = _currentTickProperty

    /**
     * Increases the current tick, publishes the corresponding [TickEvent].
     */
    fun tick() {
        _currentTickProperty.value = nextTick
        _currentTimeProperty.updateValue(_currentTimeProperty.value.plusMinutes(1))
        log.debug(" - TICK - Sending tick $currentTick, current time: ${currentTime.value}")
        globalEventBus.publishTick(currentTick)
    }

    fun start(game: Game) {
        currentGame = game
        with(currentGame) {
            executor.scheduleAtFixedRate({
                tick()
                engine.update(context(currentTick.value))
            }, 1, 1, TimeUnit.SECONDS)
        }
    }

    fun stopGame() {
        executor.shutdown()
    }

    fun sendCommand(targetEntity: ElementEntity, conversation: Conversation, destination: Coordinate) {
        // TODO: The target would be the "radio net", for now we directly send a move to command to an element
        runBlocking {
            // TODO: The source of the event would be "HQ" which the player controls
            log.debug("Sending conversation to ${targetEntity.callsign}: $conversation")
            targetEntity.sendCommand(MoveTo(destination, currentGame.context(nextTick.value), targetEntity))
        }
    }
}

/**
 * Publishes a [TickEvent]. Or in other words: Proceed to the next tick.
 */
private fun EventBus.publishTick(tick: TickId) =
        publish(TickEvent(tick), Ticks)