package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.ui.fragments.BaseFragment
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.internal.behavior.Observable

/**
 * Fragments extending this class have the purpose to show one information about a tile in the world. This tile is
 * an [Observable] that changes by user input. A fragment may be locked/unlocked which preserves updating
 * when the observed tile changes.
 */
abstract class TileInformationFragment(
        override val width: Int,
        currentTile: ObservableValue<Coordinate>
) : BaseFragment {

    init {
        currentTile.onChange {
            if (!locked) {
                updateInformation(it.newValue)
            }
        }
    }

    private var locked = false

    /**
     * Toggles the locked state of this fragment. When the fragment is locked it does not update when the
     * observed tile changes.
     */
    fun toggleLock() {
        locked = !locked
    }

    /**
     * This method is called when the underlying observed tile updates and this fragment is not locked.
     *
     * @see toggleLock
     */
    protected abstract fun updateInformation(newCoordinate: Coordinate)
}