package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import org.hexworks.amethyst.api.base.BaseAttribute

/**
 * The attribute that contains a mutable [Vision] signal. When creating a new signal, [visualRange] should
 * be used as power.
 */
internal data class VisionAttribute(var vision: Vision, val visualRange: VisionPower = vision.power) : BaseAttribute()
