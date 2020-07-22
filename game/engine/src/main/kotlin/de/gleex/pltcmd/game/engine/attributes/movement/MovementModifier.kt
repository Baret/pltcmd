package de.gleex.pltcmd.game.engine.attributes.movement

import org.hexworks.amethyst.api.Attribute

/**
 * A movement modifier influences movement of an entity. It may for example change the speed or prevent movement altogether.
 */
interface MovementModifier: Attribute {
    /**
     * The type of a modifier describes how it affects movement.
     */
    enum class Type {
        /**
         * A mutating [MovementModifier] changes the value of the current speed.
         */
        Mutator,
        /**
         * A [MovementModifier] of this type prevents movement. This means if at least one modifier
         * of this type is present the entity can not move.
         */
        Prevention,
        /**
         * [MovementModifier] with this type cap the speed to a maximum value.
         */
        Cap
    }

    val type: Type
}