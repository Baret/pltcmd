package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.signals.vision.VisualSignal
import org.hexworks.amethyst.api.Attribute

internal data class VisionAttribute(var vision: VisualSignal, val visualRange: VisionPower) : Attribute
