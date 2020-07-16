package de.gleex.pltcmd.model.elements

/**
 * Every element (and all its subordinates) belong to a corps. That is one branch of service
 * responsible for specific kinds of tasks. Every corps has a different purpose in the field.
 */
enum class Corps {
    /**
     * The core of an army is its fighting force. This corps is the "boots on the ground", the hammer
     * to hit the nails.
     */
    Fighting,
    /**
     * The logistics corps is the backbone of every army. It moves stuff around and thus keeps
     * all elements supplied and in the right spot. The jobs of this corps are:
     *
     * - Transport: Troops as well as supplies
     * - Construction: Erect buildings, deployables and even complete bases
     */
    Logistics,
    /**
     * When a little more "boom" is required, this corps comes into play. It provides indirect
     * and direct fire support to engage enemy position over large distances. This includes:
     *
     * - Indirect fire like mortars and artillery
     * - CAS: Close air support, executed by attack choppers or planes
     * - Destruction/Sabotage: Remove enemy constructions
     */
    CombatSupport,
    /**
     * Intelligence is everything in war. Before sending troops to an area it should be scouted
     * first. This corps helps revealing unknown parts of the map and spot and track enemy elements.
     */
    Reconnaissance
}