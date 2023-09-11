package de.gleex.pltcmd.game.application.graphics

import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings
import java.io.File

// TODO: programmatically pack the textures into an atlas
fun main() {
    println("processing terrain folder")
    val baseFolder = ClassLoader.getSystemClassLoader().getResource("terrain/")?.path!!
    val settings = Settings().apply {
        paddingX = 2
        paddingY = 2
        maxWidth = 104
        //rotation
    }
    TexturePacker.process(settings, baseFolder, "$baseFolder${File.pathSeparator}packed", "terrain")
    println("texture packing done")
}