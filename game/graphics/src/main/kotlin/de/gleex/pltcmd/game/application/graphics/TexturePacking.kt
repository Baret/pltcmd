package de.gleex.pltcmd.game.application.graphics

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings

fun main() {
    packTerrain()
    packElements()
}

private fun packTerrain() {
    val baseFolder = "game/graphics/src/main/resources/terrain/"
    println("Packing textures in $baseFolder")
    val settings = Settings().apply {
        pot = false
        grid = true
        duplicatePadding = true
        paddingX = 4
        paddingY = 4
        maxWidth = 128
    }
    TexturePacker.process(settings, baseFolder, "${baseFolder}packed", "terrain")
    println("texture packing done")
}

private fun packElements() {
    val baseFolder = "game/graphics/src/main/resources/elements/"
    println("Packing textures in $baseFolder")
    val settings = Settings().apply {
        pot = false
        grid = true
        paddingX = 4
        paddingY = 4

        filterMin = Texture.TextureFilter.MipMapLinearNearest
        filterMag = Texture.TextureFilter.Linear

        // not sure if we really want that
        stripWhitespaceX = true
    }
    TexturePacker.process(settings, baseFolder, "${baseFolder}packed", "elements")
    println("elements packing done")
}