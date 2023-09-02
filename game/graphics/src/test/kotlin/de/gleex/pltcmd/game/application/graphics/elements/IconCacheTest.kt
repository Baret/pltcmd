package de.gleex.pltcmd.game.application.graphics.elements

import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.Headers2
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.file.exist
import io.kotest.matchers.file.haveExtension
import io.kotest.matchers.file.shouldNotHaveFileSize
import io.kotest.matchers.should
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.exhaustive
import java.io.File
import java.io.FileNotFoundException

class IconCacheTest : WordSpec() {
    init {
        "Loading all element icons without modifier" should {
            "return a valid URL to a PNG image" {
                checkAll<Affiliation, ElementKind, Rung> { affiliation, elementKind, rung ->
                    val selector = ElementIconSelector(affiliation, elementKind, rung)
                    val uri = IconCache.load(selector).toURI()
                    assertSoftly(File(uri)) {
                        it should exist()
                        it should haveExtension("png")
                        it shouldNotHaveFileSize (0L)
                    }
                }
            }
        }

        "Tags" should {
            "throw exception, if invalid" {
                checkAll<Affiliation, ElementKind, Rung, String> { affiliation, elementKind, rung, randomModifier ->
                    shouldThrow<FileNotFoundException> {
                        IconCache.load(
                            ElementIconSelector(
                                affiliation,
                                elementKind,
                                rung,
                                listOf(randomModifier)
                            )
                        )
                    }
                }
            }
            "be present for certain element kinds" {
                table(
                    Headers2("ElementKind", "available Tags"),
                    // map ElementKind to expected list of tags (each tag is tested alone)
                    row(ElementKind.Infantry, listOf("aa", "at", "arty", "engi", "snip", "recon")),
                    row(ElementKind.Armored, listOf("recon"))
                ).forAll { elementKind, availableTags ->
                    checkAll(
                        availableTags.exhaustive(),
                        Exhaustive.enum<Affiliation>(),
                        Exhaustive.enum<Rung>()
                    ) { tag, affiliation, rung ->
                        shouldNotThrowAny {
                            IconCache.load(
                                ElementIconSelector(
                                    affiliation,
                                    elementKind,
                                    rung,
                                    listOf(tag)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}