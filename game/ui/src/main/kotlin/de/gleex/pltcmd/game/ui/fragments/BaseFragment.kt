package de.gleex.pltcmd.game.ui.fragments

import org.hexworks.zircon.api.component.Fragment

/**
 * Base class for all fragments.
 */
interface BaseFragment: Fragment {
    /**
     * the width for the root Component to use
     */
    val width: Int
}