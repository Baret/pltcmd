package de.gleex.pltcmd.game.application.graphics.elements

import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Affiliation
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.file.exist
import io.kotest.matchers.should
import io.kotest.property.checkAll
import java.io.File

class IconCacheTest : WordSpec() {
    init {
        "Loading all icons without modifier" should {
            "return a valid URL" {
                checkAll<Affiliation, ElementKind, Rung> { affiliation, elementKind, rung ->
                    val selector = ElementIconSelector(affiliation, elementKind, rung)
                    val uri = IconCache.load(selector).toURI()
                    File(uri) should exist()
                }
            }
        }
    }
}