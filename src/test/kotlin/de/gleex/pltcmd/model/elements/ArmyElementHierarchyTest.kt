package de.gleex.pltcmd.model.elements

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ArmyElementHierarchyTest {

	@Test
	fun createElement() {
		val division = ArmyElementHierarchy.Division.createElement(null)

		assertHierarchy(
			division,
			3/*brigades*/,
			4/*battalions*/,
			4/*companies*/,
			5/*platoons*/,
			4/*squads*/,
			2/*fireteams*/,
			1/*buddy team*/
		)
		assertEquals(7024, GenericUnit.IdCounter.next())
	}

	private fun assertHierarchy(actual: Element, vararg expectedCounts: Int) {
		assertNotNull(actual)

		val actualSubordinates = actual.getSubordinates()
		val actualMembers = actual.members
		if (actualSubordinates.isEmpty()) {
			assertBuddyTeam(actualMembers)
		} else {
			assertLeader(actualMembers)
			assertSubHierarchy(actual, *expectedCounts)
		}
	}

	private fun assertLeader(actualMembers: Set<Unit>) {
		assertEquals(1, actualMembers.size)
		assertSoldiers(actualMembers)
	}

	private fun assertBuddyTeam(actualMembers: Set<Unit>) {
		assertEquals(2, actualMembers.size)
		assertSoldiers(actualMembers)
	}
	
	private fun assertSoldiers(actualMembers: Set<Unit>) {
		actualMembers.forEach() {
			assertTrue(it.isOfType(UnitType.Soldier))
		}
	}

	private fun assertSubHierarchy(actual: Element, vararg expectedCounts: Int) {
		val expectedSubordinateCounts = expectedCounts.firstOrNull() ?: 0;
		val remainingCounts = expectedCounts.drop(1)
		val actualSubordinates = actual.getSubordinates()
		assertEquals(expectedSubordinateCounts, actualSubordinates.size)
		actualSubordinates.forEach {
			assertEquals(actual, it.superordinate)
			assertHierarchy(it, *remainingCounts.toIntArray())
		}
	}

}