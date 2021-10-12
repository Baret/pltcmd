package de.gleex.pltcmd.game.ticks

import de.gleex.pltcmd.game.options.GameConstants
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

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

    private val _currentDayProperty: Property<Int> = 0.toProperty { newDay -> newDay > 0 }

    private val _isPausedProperty: Property<Boolean> = true.toProperty()

    private var executor = newExecutor()

    init {
        val initialTime = LocalTime.of(23, 50)
        _currentTimeProperty = createPropertyFrom(initialTime)
        _currentTimeProperty.onChange { changedTime ->
            if(changedTime.newValue.toSecondOfDay() == 0) {
                _currentDayProperty.transformValue { it + 1 }
            }
        }
    }

    /**
     * The current ingame time as [ObservableValue] so that it is possible to listen for changes.
     */
    val currentTime: ObservableValue<LocalTime> = _currentTimeProperty
    /**
     * The current ingame time converted to a string as [ObservableValue] so that it is possible to listen for changes.
     */
    val currentTimeString: ObservableValue<String> = _currentTimeProperty.bindTransform { localTime -> localTime.toString() }
    /**
     * The current ingame day starting at "day 0".
     */
    val currentDay: ObservableValue<Int> = _currentDayProperty
    /**
     * The [currentTick] but observable.
     */
    val currentTickObservable: ObservableValue<TickId> = _currentTickProperty

    /**
     * Observable flag to see if the game is currently paused.
     */
    val isPaused: ObservableValue<Boolean> = _isPausedProperty

    private var lastTickTimestamp: Long? = null

    private var durationMeasurements: Int = 0
    private var avgTickDuration: Double = 0.0

    /**
     * Increases the current tick, publishes the corresponding [TickEvent].
     */
    @OptIn(ExperimentalTime::class)
    private fun tick() {
        if(log.isDebugEnabled() && lastTickTimestamp != null) {
            val last = lastTickTimestamp ?: 0
            val duration = System.currentTimeMillis() - last
            avgTickDuration = ((avgTickDuration * durationMeasurements) + duration) / ++durationMeasurements
            log.debug("$currentTick took ${duration.toDuration(TimeUnit.MILLISECONDS)}. Avg = $avgTickDuration")
            lastTickTimestamp = System.currentTimeMillis()
        }
        _currentTickProperty.value = nextTick
        _currentTimeProperty.updateValue(
                _currentTimeProperty
                        .value
                        .plusSeconds(GameConstants.Time.timeSimulatedPerTick.inWholeSeconds))
        log.debug(" - TICK - Sending tick $currentTick, current time: ${currentTime.value}")
        globalEventBus.publishTick(currentTick)
    }

    /**
     * Starts the ticker with the values from [GameOptions].
     */
    fun start() =
            start(GameOptions.TickRate.duration, GameOptions.TickRate.timeUnit)

    /**
     * Starts the ticker with the given duration between each tick.
     */
    fun start(tickRateDuration: Long, tickRateTimeunit: TimeUnit) {
        executor.scheduleAtFixedRate({
            tick()
        }, 1, tickRateDuration, tickRateTimeunit)
        _isPausedProperty.updateValue(false)
        lastTickTimestamp = System.currentTimeMillis()
    }

    /**
     * Stops the ticker and thus advancement of ingame time (aka "game paused")
     */
    fun stop() {
        executor.shutdown()
        executor = newExecutor()
        _isPausedProperty.updateValue(true)
        lastTickTimestamp = null
    }

    /**
     * Starts/stops the ticker depending on [isPaused].
     */
    fun togglePause() {
        if(isPaused.value) {
            start()
        } else {
            stop()
        }
    }

    private fun newExecutor() = Executors.newScheduledThreadPool(1)
}

/**
 * Publishes a [TickEvent]. Or in other words: Proceed to the next tick.
 */
private fun EventBus.publishTick(tick: TickId) =
        publish(TickEvent(tick), Ticks)