package de.gleex.pltcmd.game.engine.attributes.goals.testimplementations

import de.gleex.pltcmd.game.engine.attributes.goals.Goal

/**
 * Simply inherits from [Goal] and uses the default implementations.
 */
class TestGoal(vararg subGoals: Goal) : Goal(*subGoals)