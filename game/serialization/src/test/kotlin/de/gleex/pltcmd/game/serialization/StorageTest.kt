package de.gleex.pltcmd.game.serialization

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import java.io.File

class StorageTest : StringSpec({

    "sanitizeFilename should create safe names for files" {
        forAll(
            row("simple", "0f7d0d088b6ea936fb25b477722d734706fe8b40"),
            row("_3-Om", "6cfd763a512deeb80b5907a89f0d473e02fb0b09"),
            row("Süßölgefäß", "7aeb4ad3e3d475b3ffbcf5e9b29d797c5eb719ec"),
            row("1337", "77ba9cd915c8e359d9733edcfe9c61e5aca92afb"),
            // cactus emoji
            row("\uD83C\uDF35", "630b7aec7c76806f4613ec9841bff48b845534f8"),
            row("foo/bar.baz", "512ceb063fd31e3e8159a3e88bddfc5d8f62b161"),
            row("foo.bar.baz", "fb2d5a853a8edd799b4084a828d7d6746210534b"),
            row(".foo", "7770702de319e88ed55826fa88d448ba7ca628d0"),
            row("/test", "f133a4599372cf531bcdbfeb1116b9afe8d09b4f"),
            row("/test/", "5411388e827eca295edece717228823dd2fe055c"),
            row("../test", "54d0ecf469c7bea98493349d3fcc4915b598ed98"),
            row("C:\\Users\\cod3r", "5683e660912624aa6dfa241858ae06c7abeb05a3"),
            // first ASCII special chars
            row(" !\"#\$%&'()*+,-.", "64afe078dbdc5a59629f935ddf07ef6b20daa9a2"),
            // second block of ASCII special chars behind numbers
            row(":;<=>?@", "c31ed54d39d11b9b37f525dcfd40581d56ba1fba"),
            // special ASCII chars behind upper case chars
            row("[\\]^_`", "406a370d0c90527298f1d93e055bec44b659a7d6"),
            // special ASCII chars behind lower case chars
            row("{|}~", "42841c73ca4dc5ec5360f1f7186a8411d3ec35cb"),
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
