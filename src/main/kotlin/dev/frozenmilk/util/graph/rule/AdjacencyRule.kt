package dev.frozenmilk.util.graph.rule

import dev.frozenmilk.util.graph.Graph
import java.lang.FunctionalInterface

@FunctionalInterface
fun interface AdjacencyRule<NODE: Any> {
	operator fun invoke(graph: Graph<NODE>)
}