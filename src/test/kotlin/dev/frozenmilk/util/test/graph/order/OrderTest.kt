package dev.frozenmilk.util.test.graph.order

import dev.frozenmilk.util.graph.Graph
import dev.frozenmilk.util.graph.emitGraph
import dev.frozenmilk.util.graph.rule.dependsOn
import dev.frozenmilk.util.graph.rule.independent
import dev.frozenmilk.util.graph.sort
import dev.frozenmilk.util.test.graph.TestNode
import org.junit.Assert
import org.junit.Test

class OrderTest {
	@Test
	fun order() {
		val order = setOf(One, Two).emitGraph { it.adjacencyRule }.sort()
		Assert.assertEquals(One, order[0])
		Assert.assertEquals(Two, order[1])
	}
}
private object One : TestNode {
	override val adjacencyRule = independent<TestNode, Graph<TestNode>>()
}
private object Two : TestNode {
	override val adjacencyRule = dependsOn(One)
}