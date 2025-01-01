package dev.frozenmilk.util.test.graph.cycle

import dev.frozenmilk.util.graph.rule.dependsOn
import dev.frozenmilk.util.graph.emitGraph
import dev.frozenmilk.util.graph.sort
import dev.frozenmilk.util.test.graph.TestNode
import org.junit.Assert
import org.junit.Test
import org.junit.function.ThrowingRunnable

class CycleTest {
	@Test
	fun simpleCycle() {
		Assert.assertThrows(IllegalStateException::class.java) {
			setOf(One, Two).emitGraph { it.adjacencyRule }.sort()
		}
	}
}

private object One : TestNode {
	override val adjacencyRule = dependsOn(Two)
}
private object Two : TestNode {
	override val adjacencyRule = dependsOn(One)
}
