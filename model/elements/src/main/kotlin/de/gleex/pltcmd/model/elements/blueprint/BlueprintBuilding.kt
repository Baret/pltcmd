package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.Corps
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.elements.units.Units

/**
 * The start of an [ElementBlueprint]. It sets the basic values for the resulting element. After
 * these are set units can be added by calling [consistingOf] (possibly as infix function).
 *
 * @sample a
 */
internal class ElementBlueprintStart(
        private val corps: Corps,
        private val kind: ElementKind,
        private val rung: Rung
) {
    /**
     * Returns an actual [ElementBlueprint] which can be used to create an instance of an element
     * or to create a commanding element by chaining [ElementBlueprint.commanding].
     */
    infix fun consistingOf(units: List<Units>) = ElementBlueprint(corps, kind, rung, units)

    /**
     * Returns an actual [ElementBlueprint] which can be used to create an instance of an element
     * or to create a commanding element by chaining [ElementBlueprint.commanding].
     */
    infix fun consistingOf(unit: Units) = consistingOf(listOf(unit))
}

/**
 * To start declaring an [ElementBlueprint] you first need an [ElementBlueprintStart]. It sets
 * the basic values for the resulting element.
 */
internal typealias a = ElementBlueprintStart

/**
 * When adding this method to the building process the resulting element will be a [CommandingElement].
 *
 * @return a new [CommandingElementBlueprint] with the given subordinates
 */
internal infix fun AbstractElementBlueprint<*>.commanding(subordinates: List<AbstractElementBlueprint<*>>): CommandingElementBlueprint =
        when(this) {
            is ElementBlueprint           -> CommandingElementBlueprint(corps, kind, rung, units, subordinates)
            is CommandingElementBlueprint -> CommandingElementBlueprint(corps, kind, rung, units, subordinates + this.subordinates)
        }

/**
 * When an element blueprint is turned into a [CommandingElementBlueprint] but does not (yet) have any subordinates.
 */
internal val noSubordinates = emptyList<AbstractElementBlueprint<*>>()