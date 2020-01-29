package de.gleex.pltcmd.model.elements

/**
 * Part of a military hierarchy with a set of [Unit]s and subordinate [Element]s.
 */
data class Element(val callSign: CallSign, val members: Set<Unit>, val superordinate: Element? = null) {
    private val _subordinates: MutableSet<Element> = mutableSetOf()

    /**
     * The count of all units in this Element and all its subordinates
     */
    val allUnits: Int
        get() { return members.size + subordinates.map { it.allUnits }.sum() }

    init {
        // link from element to its subordinates
        superordinate?.addSubordinate(this)
    }

    // visible for test
    internal val subordinates: Set<Element>
        get() = _subordinates.toSet()

    private fun addSubordinate(subordinate: Element) {
        // prevent circles to keep a tree hierarchy
        if (subordinate == this || isSuperordinate(subordinate)) {
            throw IllegalArgumentException("Cannot add ${subordinate} as subordinate because it is a superordinate of this element!")
        }
        _subordinates.add(subordinate)
    }

    private fun isSuperordinate(element: Element): Boolean {
        return superordinate == element || superordinate?.isSuperordinate(element) ?: false
    }

    override fun toString() = callSign.toString()

}