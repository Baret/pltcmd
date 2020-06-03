package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.CallSign
import kotlinx.collections.immutable.PersistentSet
import org.hexworks.cobalt.databinding.api.collection.ObservableSet
import org.hexworks.cobalt.databinding.api.event.ObservableValueChanged

/**
 * When a [CommandingElement] has a superordinate itself it loses its own callsign and instead
 * its superordinate determines the callsign. This class keeps track of the subordinates of
 * an element and makes sure they keep the same "sub callsign" as long as they are assigned.
 */
class SubCallsignProvider(private val parentCallsign: () -> CallSign, private val observedSubordinates: ObservableSet<Element>) {

    private var currentFreeIndex = 1

    private val callsignMap: MutableMap<Element, Int> = observedSubordinates
            .value
            .associateWith { currentFreeIndex++ }
            .toMutableMap()

    init {
        observedSubordinates.onChange { change ->
            if(deletionHappened(change)) {
                val deleted: Set<Element> = change.oldValue - change.newValue
                deleted
                        .filter { callsignMap.containsKey(it) }
                        .forEach {
                            updateIndex(callsignMap[it]!!)
                            callsignMap.remove(it)
                        }
            } else if(additionHappened(change)) {
                val added = change.newValue - change.oldValue
                added
                        .forEach {
                            callsignMap[it] = currentFreeIndex
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
                    (callsignMap.values.max() ?: 0) + 1
                }
    }

    private fun deletionHappened(change: ObservableValueChanged<PersistentSet<Element>>) =
            change.newValue.size < change.oldValue.size

    private fun additionHappened(change: ObservableValueChanged<PersistentSet<Element>>) =
            change.newValue.size > change.oldValue.size

    fun callSignFor(subordinate: Element): CallSign {
        require(callsignMap.containsKey(subordinate)) {
            "Cannot provide subcallsign for '$parentCallsign' for unknown subordinate $subordinate"
        }
        return parentCallsign.invoke() + "-${callsignMap[subordinate]!!}"
    }
}