package de.gleex.pltcmd.util.collections


/** Applies each filter one after another and checks if only a single element is found. When all filters are applied all matching elements are given. */
fun <T> Collection<T>.filterUntilFound(vararg filters: (T) -> Boolean): Set<T> {
    if (isEmpty()) {
        return emptySet()
    }
    if (filters.isEmpty()) {
        return toSet()
    }
    val filter = filters[0]
    val filtered = filter(filter)
    if (filtered.size == 1) {
        return setOf(filtered[0])
    }
    val remainingFilters = filters.sliceArray(1 until filters.size)
    return filterUntilFound(*remainingFilters)
}
