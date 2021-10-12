package de.gleex.pltcmd.util.knowledge

/**
 * Determines how much knowledge (details) one has about a thing.
 */
enum class KnowledgeGrade {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    FULL;

    companion object {
        /**
         * Maps the percentage or ratio of known things to a [KnowledgeGrade]. The grade is determined linearly for all grades.
         */
        fun of(percentage: Double): KnowledgeGrade {
            return when {
                percentage >= 1.0  -> FULL
                percentage >= 0.67 -> HIGH
                percentage >= 0.33 -> MEDIUM
                percentage > 0.0   -> LOW
                else               -> NONE
            }
        }
    }
}
