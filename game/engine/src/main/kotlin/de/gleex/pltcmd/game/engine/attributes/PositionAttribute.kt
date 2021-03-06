package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.base.BaseAttribute
import org.hexworks.cobalt.databinding.api.property.Property

/** The location of an entity on the map. */
internal class PositionAttribute(val coordinate: Property<Coordinate>) : BaseAttribute()