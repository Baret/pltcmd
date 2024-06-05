package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveMaxLength

class TerrainTransformationsTest : WordSpec({
    "All transformations for terrain height" should {
        "be valid" {
            Format.entries.toTypedArray()
                    .forAll { format ->
                        TerrainHeight.entries.toTypedArray()
                                .forAll { terrainHeight ->
                                    assertSoftly {
                                        val directlyTransformed = terrainHeight.terrainHeightTransformation(format)
                                        directlyTransformed shouldHaveMaxLength format.length
                                        terrainHeight.toFrontendString(format).value shouldBe directlyTransformed
                                    }
                                }
                    }
        }
    }

    "All transformations for terrain type" should {
        "be valid" {
            Format.entries.toTypedArray()
                    .forAll { format ->
                        TerrainType.entries.toTypedArray()
                                .forAll { terrainType ->
                                    assertSoftly {
                                        val directlyTransformed = terrainType.terrainTypeTransformation(format)
                                        directlyTransformed shouldHaveMaxLength format.length
                                        terrainType.toFrontendString(format).value shouldBe directlyTransformed
                                    }
                                }
                    }
        }
    }

    "All transformations for every terrain combination" should {
        "be valid" {
            Format.entries.toTypedArray()
                    .forAll { format ->
                        TerrainType.entries.toTypedArray()
                                .forAll { type ->
                                    TerrainHeight.entries.toTypedArray().forAll { height ->
                                        assertSoftly {
                                            val terrain = Terrain.of(type, height)
                                            val directlyTransformed = terrain.terrainTransformation(format)
                                            directlyTransformed shouldHaveMaxLength format.length
                                            terrain.toFrontendString(format).value shouldBe directlyTransformed
                                        }
                                    }
                                }
                    }
        }
    }
})