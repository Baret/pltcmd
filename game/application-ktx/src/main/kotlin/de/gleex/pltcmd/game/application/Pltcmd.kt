package de.gleex.pltcmd.game.application

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapLinearNearest
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.FitViewport
import de.gleex.pltcmd.game.application.graphics.elements.ElementIconSelector
import de.gleex.pltcmd.game.application.graphics.elements.IconCache
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.faction.Affiliation
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import kotlin.random.Random

class Pltcmd : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val stage = Stage(FitViewport(50f, 50f))
    private val textureAtlas = TextureAtlas(Gdx.files.getFileHandle("terrain/packed/terrain.atlas", Files.FileType.Classpath))

    override fun show() {
        val grasslandsRegions = textureAtlas.findRegions("grasslands")
        val forestRegions = textureAtlas.findRegions("forest")
        val hillsRegions = textureAtlas.findRegions("hills")
        val mountainRegions = textureAtlas.findRegions("mountain")
        val waterDeepRegions = textureAtlas.findRegions("water_deep")
        val waterShallowRegions = textureAtlas.findRegions("water_shallow")
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

        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Hostile, ElementKind.Armored))
        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Friendly, ElementKind.Infantry, tags = listOf("recon")))
        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Unknown, ElementKind.Aerial))
        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Neutral, ElementKind.Infantry, tags = listOf("engi")))
        placeElementIconAtRandomPosition(ElementIconSelector(Affiliation.Self, ElementKind.MechanizedInfantry))
    }

    private fun placeElementIconAtRandomPosition(iconSelector: ElementIconSelector) {
        val imageTexture = Texture(Gdx.files.classpath(IconCache.pathFor(iconSelector)), true)
            .apply {
                when(iconSelector.affiliation) {
                    // failed: nearest, MipMapNearestLinear
                    // best result: MipMapLinearNearest
                    // favorite: setFilter(MipMapLinearNearest, Linear)
                    Affiliation.Unknown  -> setFilter(MipMapLinearNearest, Linear)
                    Affiliation.Self     -> setFilter(MipMapLinearNearest, Linear)
                    Affiliation.Friendly -> setFilter(MipMapLinearNearest, Linear)
                    Affiliation.Neutral  -> setFilter(MipMapLinearNearest, Linear)
                    Affiliation.Hostile  -> setFilter(MipMapLinearNearest, Linear)
                    null                 -> setFilter(MipMapLinearNearest, Linear)
                }
            }
        stage.addActor(
            Image(imageTexture).apply {
                val aspectRatio = imageTexture.width.toFloat() / imageTexture.height
                val posCorrection = (aspectRatio - 1) / 2f
                println("imageSize: ${imageTexture.width} * ${imageTexture.height}")
                println("aspect = $aspectRatio posCorrection = $posCorrection")
                setPosition(
                    Random.nextInt(40).toFloat() - posCorrection,
                    Random.nextInt(40).toFloat()
                )
                setSize(2.5f, 2.5f)
                setScaling(Scaling.fillY)
            }
        )
    }

    private fun drawMap(
        xRange: IntRange,
        yRange: IntRange,
        grasslandsRegions: Array<AtlasRegion>
    ) {
        for (x in xRange) {
            for (y in yRange) {
                val region = grasslandsRegions.random()
                stage.addActor(
                    Image(AtlasRegion(region)).apply {
                        setPosition(x.toFloat(), y.toFloat())
                        setSize(1f, 1f)
                        setScaling(Scaling.fill)
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
