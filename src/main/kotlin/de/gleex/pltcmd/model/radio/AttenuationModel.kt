package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain

interface AttenuationModel {
    fun reducedAt(signalStrength: Double, t: Terrain): Double
}