package de.gleex.pltcmd.game.application.actors.terrain.model

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NeighborBitmapTest : FreeSpec() {
    init {
        "Empty bitmap" {
            val bitmap = NeighborBitmap.of()

            assertSoftly(bitmap) {
                it.isNorth() shouldBe false
                it.isNorthEast() shouldBe false
                it.isEast() shouldBe false
                it.isSouthEast() shouldBe false
                it.isSouth() shouldBe false
                it.isSouthWest() shouldBe false
                it.isWest() shouldBe false
                it.isNorthWest() shouldBe false
                it.all() shouldBe false
            }
        }

        "Single direction" - {
            "north" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.NORTH)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe true
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "north east" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.NORTH_EAST)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe false
                    it.isNorthEast() shouldBe true
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "east" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.EAST)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe false
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe true
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "south east" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.SOUTH_EAST)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe false
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe true
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "south" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.SOUTH)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe false
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe true
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "south west" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.SOUTH_WEST)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe false
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe true
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "west" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.WEST)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe false
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe true
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "north west" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.NORTH_WEST)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe false
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe true
                    it.all() shouldBe false
                }
            }
        }

        "Multiple directions" - {
            "two" {
                val bitmap = NeighborBitmap.of(NeighborBitmap.Direction.NORTH, NeighborBitmap.Direction.NORTH_EAST)
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe true
                    it.isNorthEast() shouldBe true
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe false
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "three" {
                val bitmap = NeighborBitmap.of(
                    NeighborBitmap.Direction.NORTH,
                    NeighborBitmap.Direction.WEST,
                    NeighborBitmap.Direction.SOUTH_WEST
                )
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe true
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe false
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe false
                    it.isSouthWest() shouldBe true
                    it.isWest() shouldBe true
                    it.isNorthWest() shouldBe false
                    it.all() shouldBe false
                }
            }
            "five" {
                val bitmap = NeighborBitmap.of(
                    NeighborBitmap.Direction.NORTH,
                    NeighborBitmap.Direction.WEST,
                    NeighborBitmap.Direction.SOUTH,
                    NeighborBitmap.Direction.EAST,
                    NeighborBitmap.Direction.NORTH_WEST
                )
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe true
                    it.isNorthEast() shouldBe false
                    it.isEast() shouldBe true
                    it.isSouthEast() shouldBe false
                    it.isSouth() shouldBe true
                    it.isSouthWest() shouldBe false
                    it.isWest() shouldBe true
                    it.isNorthWest() shouldBe true
                    it.all() shouldBe false
                }
            }
            "all" {
                val bitmap = NeighborBitmap.of(
                    NeighborBitmap.Direction.NORTH,
                    NeighborBitmap.Direction.NORTH_WEST,
                    NeighborBitmap.Direction.SOUTH,
                    NeighborBitmap.Direction.SOUTH_WEST,
                    NeighborBitmap.Direction.SOUTH_EAST,
                    NeighborBitmap.Direction.WEST,
                    NeighborBitmap.Direction.EAST,
                    NeighborBitmap.Direction.NORTH_EAST
                )
                assertSoftly(bitmap) {
                    it.isNorth() shouldBe true
                    it.isNorthEast() shouldBe true
                    it.isEast() shouldBe true
                    it.isSouthEast() shouldBe true
                    it.isSouth() shouldBe true
                    it.isSouthWest() shouldBe true
                    it.isWest() shouldBe true
                    it.isNorthWest() shouldBe true
                    it.all() shouldBe true
                }
            }
        }
    }
}