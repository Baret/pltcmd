package de.gleex.pltcmd.game.serialization

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import java.io.File

class StorageTest : StringSpec({

    val loremIpsum256 =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "

    "sanitizeFilename should create safe names for files" {
        forAll(
            row("simple", "73696d706c65"),
            row("_3-Om", "5f332d4f6d"),
            row("Süßölgefäß", "53c3bcc39fc3b66c676566c3a4c39f"),
            row("1337", "31333337"),
            row(
                // 555 characters
                "${loremIpsum256}sanctus est Lorem ipsum dolor sit amet. ${loremIpsum256}san",
                "4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e736574657475722073616469707363696e6720656c6974722c20736564206469616d206e6f6e756d79206569726d6f642074656d706f7220696e766964756e74207574206c61626f726520657420646f6c6f7265206d61676e"
            ),
            row(
                // other end as before but same prefix
                "${loremIpsum256}this does not matter for the current implementation",
                "4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e736574657475722073616469707363696e6720656c6974722c20736564206469616d206e6f6e756d79206569726d6f642074656d706f7220696e766964756e74207574206c61626f726520657420646f6c6f7265206d61676e"
            ),
            // cactus emoji
            row("\uD83C\uDF35", "f09f8cb5"),
            row("foo/bar.baz", "666f6f2f6261722e62617a"),
            row("foo.bar.baz", "666f6f2e6261722e62617a"),
            row(".foo", "2e666f6f"),
            row("/test", "2f74657374"),
            row("/test/", "2f746573742f"),
            row("../test", "2e2e2f74657374"),
            row("C:\\Users\\cod3r", "433a5c55736572735c636f643372"),
            // first ASCII special chars
            row(" !\"#\$%&'()*+,-.", "202122232425262728292a2b2c2d2e"),
            // second block of ASCII special chars behind numbers
            row(":;<=>?@", "3a3b3c3d3e3f40"),
            // special ASCII chars behind upper case chars
            row("[\\]^_`", "5b5c5d5e5f60"),
            // special ASCII chars behind lower case chars
            row("{|}~", "7b7c7d7e"),
        ) { input, expected ->

            val result = sanitizeFilename(input)
            result shouldBe expected
            // file must be creatable
            val testFile = File.createTempFile("tmp$result", "test")
            testFile.exists() shouldBe true
            testFile.delete() shouldBe true
        }
    }
})
