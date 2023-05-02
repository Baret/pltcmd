package de.gleex.pltcmd.game.application.korge.elements.icons

import com.soywiz.korim.vector.format.SVG
import com.soywiz.korim.vector.format.readSVG
import com.soywiz.korio.file.std.resourcesVfs

/**
 * Loads and caches element icons.
 */
object IconCache {
    suspend fun load(iconSelector: IconSelector): SVG {
        return resourcesVfs["elements/${iconSelector.fileName}.svg"].readSVG()
    }
}