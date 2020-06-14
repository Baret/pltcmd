package de.gleex.pltcmd.model.elements

import kotlinx.collections.immutable.PersistentSet
import org.hexworks.cobalt.databinding.api.collection.ObservableSet
import org.hexworks.cobalt.databinding.api.event.ObservableValueChanged

/**
 * When a [CommandingElement] has a superordinate itself it loses its own callsign and instead
 * its superordinate determines the callsign. This class keeps track of the subordinates of
 * an element and makes sure they keep the same "sub callsign" as long as they are assigned.
 */
class SubCallSignProvider(private val parentCallsign: () -> CallSign, observedSubordinates: ObservableSet<Element>) {

    private var currentFreeIndex = 1

    private val callSignMap: MutableMap<Element, Int> = observedSubordinates
            .value
            .associateWith { currentFreeIndex++ }
            .toMutableMap()

    init {
        observedSubordinates.onChange { change ->
            if(deletionHappened(change)) {
                val deleted: Set<Element> = change.oldValue - change.newValue
                deleted
                    .filter { callSignMap.containsKey(it) }
                    .forEach {
                        updateIndex(callSignMap[it]!!)
                        callSignMap.remove(it)
                    }
            } else if(additionHappened(change)) {
                val added = change.newValue - change.oldValue
                added
                    .forEach {
                        callSignMap[it] = currentFreeIndex
                        updateIndex()
                    }
            }
        }
    }

    private fun updateIndex(i: Int = currentFreeIndex) {
        currentFreeIndex =
                if(i < currentFreeIndex) {
                    i
                } else {
                    (callSignMap.values.max() ?: 0) + 1
                }
    }

    private fun deletionHappened(change: ObservableValueChanged<PersistentSet<Element>>) =
            change.newValue.size < change.oldValue.size

    private fun additionHappened(change: ObservableValueChanged<PersistentSet<Element>>) =
            change.newValue.size > change.oldValue.size

    fun callSignFor(subordinate: Element): CallSign {
        require(callSignMap.containsKey(subordinate)) {
            "Cannot provide subcallsign for '$parentCallsign' for unknown subordinate $subordinate"
        }
        return parentCallsign.invoke() + "-${callSignMap[subordinate]!!}"
    }
}