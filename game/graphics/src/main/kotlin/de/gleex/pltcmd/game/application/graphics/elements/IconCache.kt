package de.gleex.pltcmd.game.application.graphics.elements

import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.io.FileNotFoundException
import java.net.URL

/**
 * Loads and caches element icons.
 */
object IconCache {
    private val log = KotlinLogging.logger { }

    /**
     * @return the URL of the icon for the given [ElementIconSelector].
     *
     * @throws FileNotFoundException if no resource for the given selector could be loaded.
     */
    suspend fun load(iconSelector: ElementIconSelector): URL {
        val fileName = pathFor(iconSelector)
        log.debug { "Loading PNG from iconSelector $iconSelector: $fileName" }
        return IconCache::class.java.classLoader.getResource(fileName)
            ?: throw FileNotFoundException(fileName)
    }

    /**
     * @return the internal path on the classpath to the file for the given selector.
     */
    fun pathFor(iconSelector: ElementIconSelector) =
        "elements/${iconSelector.fileName}.png"

    /**O
     * @return the URL of the icon for the given [ElementIconSelector].
     *
     * @throws FileNotFoundException if no resource for the given selector could be loaded.
     */
    fun loadBlocking(iconSelector: ElementIconSelector): URL {
        return runBlocking(Dispatchers.IO) {
            load(iconSelector)
        }
    }
}


val ElementIconSelector.fileName: String
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

/**
 * Later, when we have icon for element size, we use this part of the file name.
 */
@Suppress("SameReturnValue")
private fun Rung?.fileNamePart() = when (this) {
    Rung.Fireteam  -> "siz"
    Rung.Squad     -> "siz"
    Rung.Platoon   -> "siz"
    Rung.Company   -> "siz"
    Rung.Battalion -> "siz"
    null           -> "siz"
}

private fun List<String>.fileNameParts() = joinToString { "_$it" }