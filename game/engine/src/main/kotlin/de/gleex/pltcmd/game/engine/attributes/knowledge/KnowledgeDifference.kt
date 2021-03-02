package de.gleex.pltcmd.game.engine.attributes.knowledge

/** Result of the comparison of two remembered facts of the same thing. */
enum class KnowledgeDifference {
    /** The new knowledge contains less information than is already known */
    LESS,
    /** The new knowledge contains the same information as is already known */
    SAME,
    /** The new knowledge contains additional information to that already known */
    MORE
}