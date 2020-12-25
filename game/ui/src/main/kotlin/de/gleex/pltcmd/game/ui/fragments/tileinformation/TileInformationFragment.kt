package de.gleex.pltcmd.game.ui.fragments.tileinformation

import de.gleex.pltcmd.game.ui.fragments.BaseFragment
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.internal.behavior.Observable

/**
 * Fragments extending this class have the purpose to show one information about a tile in the world. This tile is
 * an [Observable] that changes by user input. A fragment may be locked/unlocked which preserves updating
 * when the observed tile changes.
 *
 * Use [currentInfoTile] to listen for changes of the observed tile.
 */
abstract class TileInformationFragment(
        currentTile: ObservableValue<Coordinate>
) : BaseFragment {

    // create an independent Property initialized with the current value
    private val tileForInformation = currentTile.value.toProperty()

    /** The tile for which an information should be provided. */
    protected val currentInfoTile: ObservableValue<Coordinate> = tileForInformation

    private var locked = false

    init {
        currentTile.onChange {
            if (!locked) {
                tileForInformation.updateValue(it.newValue)
            }
        }
    }

    /**
     * Toggles the locked state of this fragment. When the fragment is locked it does not update when the
     * observed tile changes.
     */
    fun toggleLock() {
        locked = !locked
    }

}