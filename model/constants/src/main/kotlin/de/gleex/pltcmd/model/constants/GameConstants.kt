package de.gleex.pltcmd.model.constants

/**
 * This object holds constant values that are otherwise "just assumed". When doing calculations regarding "fixed" values
 * these constants should be used instead of local literal values.
 */
object GameConstants {
    /**
     * Constant values regarding ingame time.
     */
    object Time {
        /**
         * The amount of ingame time that passes in one tick.
         */
        const val secondsSimulatedPerTick = 60.0

        /**
         * The number of ticks needed to simulate one ingame minute.
         * @see secondsSimulatedPerTick
         */
        const val ticksPerMinute: Double = 60.0 / secondsSimulatedPerTick
    }

    /**
     * Constant values regarding the simulated world.
     */
    object World {
        /**
         * One tile of the world is square and this is the length of each edge.
         */
        const val tileSizeInMeters = 100.0
    }

    /**
     * Speed is distance per time ;)
     */
    object Speed {
        private const val secondsPerHour = 60.0 * 60.0
        private const val metersPerKilometer = 1000.0

        /**
         * To travel one tile of distance in one tick you need this speed.
         *
         * @see Time.secondsSimulatedPerTick
         * @see World.tileSizeInMeters
         */
        const val speedForOneTileInOneTickInMetersPerSecond = World.tileSizeInMeters / Time.secondsSimulatedPerTick

        /**
         * To travel one tile of distance in one tick you need this speed.
         *
         * @see Time.secondsSimulatedPerTick
         * @see World.tileSizeInMeters
         */
        const val speedForOneTileInOneTickInKph =
                // convert meters to kilometers and seconds to hours -> m/s to km/h
                speedForOneTileInOneTickInMetersPerSecond * (secondsPerHour / metersPerKilometer)
    }
}