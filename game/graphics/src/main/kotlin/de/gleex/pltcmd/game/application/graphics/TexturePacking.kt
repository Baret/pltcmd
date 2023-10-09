package de.gleex.pltcmd.game.application.graphics

import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings

fun main() {
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