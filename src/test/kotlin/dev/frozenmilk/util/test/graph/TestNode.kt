package dev.frozenmilk.util.test.graph

import dev.frozenmilk.util.graph.Graph
import dev.frozenmilk.util.graph.rule.AdjacencyRule

interface TestNode {
	val adjacencyRule: AdjacencyRule<TestNode, Graph<TestNode>>
}