package de.gleex.pltcmd.game.application.korge.elements.icons

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.vector.format.SVG
import com.soywiz.korim.vector.format.readSVG
import com.soywiz.korio.file.std.resourcesVfs
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

/**
 * Loads and caches element icons.
 */
object IconCache {
    private val log = KotlinLogging.logger { }

    suspend fun load(iconSelector: IconSelector): Bitmap {
        val fileName = "elements/${iconSelector.fileName}.png"
        log.debug { "Loading SVG from iconSelector $iconSelector: $fileName" }
        return resourcesVfs[fileName].readBitmap(PNG)
            .also { log.debug { "Loaded PNG $it" } }
    }

    fun loadBlocking(iconSelector: IconSelector): SVG {
        return runBlocking(Dispatchers.IO) {
            resourcesVfs["elements/${iconSelector.fileName}.svg"].readSVG()
        }
    }
}


private val IconSelector.fileName: String
    get() = "${affiliation.fileNamePart()}_${kind.fileNamePart()}_${size.fileNamePart()}${tags.fileNameParts()}"

private fun Affiliation?.fileNamePart() = when (this) {
    Affiliation.Unknown  -> "unk"
    Affiliation.Self     -> "fnd"
    Affiliation.Friendly -> "fnd"
    Affiliation.Neutral  -> "neu"
    Affiliation.Hostile  -> "hst"
    null                 -> "unk"
}

private fun ElementKind?.fileNamePart() = when (this) {
    ElementKind.Infantry           -> "inf"
    ElementKind.MotorizedInfantry  -> "mot"
    ElementKind.MechanizedInfantry -> "mec"
    ElementKind.Armored            -> "arm"
    ElementKind.Aerial             -> "air"
    null                           -> "knd"
}

private fun Rung?.fileNamePart() = when (this) {
    Rung.Fireteam  -> "siz"
    Rung.Squad     -> "siz"
    Rung.Platoon   -> "siz"
    Rung.Company   -> "siz"
    Rung.Battalion -> "siz"
    null           -> "siz"
}

private fun List<String>.fileNameParts() = joinToString { "_$it" }