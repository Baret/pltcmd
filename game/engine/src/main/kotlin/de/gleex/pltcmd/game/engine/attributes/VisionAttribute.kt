package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import org.hexworks.amethyst.api.Attribute

internal class VisionAttribute(var vision: Vision, val visualRange: VisionPower) : Attribute