package de.gleex.pltcmd.game.application.screens

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.FitViewport
import de.gleex.pltcmd.game.application.actors.terrain.WorldTileActor
import de.gleex.pltcmd.game.application.graphics.elements.ElementIconSelector
import de.gleex.pltcmd.game.application.graphics.elements.fileName
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import kotlin.random.Random

/**
 * First steps with libgdx/KTX. This screen is kind of a playground.
 */
class FirstScreen : KtxScreen {
    private val skin: Skin = Skin()
    private val stage = Stage(FitViewport(20f, 20f))
    private val textureAtlasTerrain =
        TextureAtlas(Gdx.files.getFileHandle("terrain/packed/terrain.atlas", Files.FileType.Classpath))
    private val textureAtlasElements =
        TextureAtlas(Gdx.files.getFileHandle("elements/packed/elements.atlas", Files.FileType.Classpath))

    override fun show() {
        Gdx.input.inputProcessor = stage

        val grasslandsRegions = textureAtlasTerrain.findRegions("grasslands")
        val forestRegions = textureAtlasTerrain.findRegions("forest")
        val hillsRegions = textureAtlasTerrain.findRegions("hills")
        val mountainRegions = textureAtlasTerrain.findRegions("mountain")
        val waterDeepRegions = textureAtlasTerrain.findRegions("water_deep")
        val waterShallowRegions = textureAtlasTerrain.findRegions("water_shallow")
        drawMap(0..49, 0..49, grasslandsRegions)
        drawMap(3..6, 2..5, forestRegions)
        drawMap(30..46, 20..49, forestRegions)
        drawMap(20..26, 21..25, forestRegions)
        // a river
        drawMap(0..10, 10..10, waterShallowRegions)
        drawMap(11..15, 10..10, waterDeepRegions)
        drawMap(8..15, 11..11, waterDeepRegions)
        drawMap(8..15, 9..9, waterDeepRegions)
        // hills and mountains
        drawMap(3..12, 12..12, hillsRegions)
        drawMap(4..9, 13..15, hillsRegions)
        drawMap(5..7, 13..13, mountainRegions)
        drawMap(9..13, 0..2, mountainRegions)

        stage.addActor(
            WorldTileActor(
                WorldTile(
                    Coordinate(100, 123),
                    Terrain.of(TerrainType.MOUNTAIN, TerrainHeight.TEN)
                )
            )
                .apply {
                    setPosition(0f, 0f)
                    setSize(1f, 1f)
                    setScale(1f)
                })

        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Hostile, ElementKind.Armored))
        placeElementIconAtRandomPosition(
            ElementIconSelector(
                Affiliation.Friendly,
                ElementKind.Infantry,
                tags = listOf("recon")
            )
        )
        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Unknown, ElementKind.Aerial))
        placeElementIconAtRandomPosition(
            ElementIconSelector(
                Affiliation.Neutral,
                ElementKind.Infantry,
                tags = listOf("engi")
            )
        )
        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Self, ElementKind.MechanizedInfantry))
    }

    private fun placeElementIconAtRandomPosition(iconSelector: ElementIconSelector) {
        val imageTexture = textureAtlasElements.findRegion(iconSelector.fileName)
        stage.addActor(Image(imageTexture).apply {
                    val aspectRatio = imageTexture.packedWidth.toFloat() / imageTexture.packedHeight
                    val posCorrection = (aspectRatio - 1) / 2f
                    println("imageSize: ${imageTexture.packedWidth} * ${imageTexture.packedHeight}")
                    println("aspect = $aspectRatio posCorrection = $posCorrection")
                    setPosition(
                        Random.nextInt(19).toFloat(),
                        Random.nextInt(19).toFloat()
                    )
                    setSize(1f, 1f)
                    setScaling(Scaling.fillY)
                }
        )
    }

    private fun drawMap(
        xRange: IntRange,
        yRange: IntRange,
        atlasRegions: Array<TextureAtlas.AtlasRegion>
    ) {
        for (x in xRange) {
            for (y in yRange) {
                val region = atlasRegions.random()
                stage.addActor(
                    Image(TextureAtlas.AtlasRegion(region)).apply {
                        setPosition(x.toFloat(), y.toFloat())
                        setSize(1f, 1f)
                        setScaling(Scaling.fill)
                        val tint = Random.nextDouble(0.4, 1.0).toFloat()
//                        setColor(255f, 255f, 255f, tint)
                    }
                )
            }
        }
    }

    override fun render(delta: Float) {
        with(stage) {
            act(delta)
            draw()
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        stage.disposeSafely()
    }
}