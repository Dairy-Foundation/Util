@file:Suppress("unused")
package dev.frozenmilk.util.graph.rule

import java.lang.Class

/**
 * no-op
 */
fun <NODE: Any> independent() = AdjacencyRule<NODE> {}

/**
 * [this] depends on [dependency]
 *
 * will crash if [dependency] doesn't exist in the graph
 */
fun <NODE: Any> NODE.dependsOn(dependency: NODE) = AdjacencyRule<NODE> { graph ->
	checkNotNull(graph[dependency]) { "$dependency was not in graph" }
	requireNotNull(graph[this@dependsOn]) { "${this@dependsOn} was not in graph" }.plus(dependency)
}

/**
 * [dependant] depends on [this]
 *
 * will crash if [dependant] doesn't exist in the graph
 */
fun <NODE: Any> NODE.dependedOn(dependant: NODE) = AdjacencyRule<NODE> { graph ->
	checkNotNull(graph[this@dependedOn]) { "${this@dependedOn} was not in graph" }
	requireNotNull(graph[dependant]) { "$dependant was not in graph" }.plus(this@dependedOn)
}

/**
 * [this] depends on [dependency]
 */
fun <NODE: Any> NODE.optionalDependsOn(dependency: NODE) = AdjacencyRule<NODE> { graph ->
	requireNotNull(graph[this@optionalDependsOn]) { "${this@optionalDependsOn} was not in graph" }.let {
		if (graph[dependency] == null) return@AdjacencyRule
		it + dependency
	}
}

/**
 * [dependant] depends on [this]
 */
fun <NODE: Any> NODE.optionalDependedOn(dependant: NODE) = AdjacencyRule<NODE> { graph ->
	checkNotNull(graph[this@optionalDependedOn]) { "${this@optionalDependedOn} was not in graph" }
	graph[dependant]?.run { plus(this@optionalDependedOn) }
}

/**
 * [this] depends on all [NODE]s of type [dependencyClass]
 *
 * will crash if no [NODE]s of type [dependencyClass] exist in the graph
 */
fun <NODE: Any> NODE.dependsOnClass(dependencyClass: Class<NODE>) = AdjacencyRule<NODE> { graph ->
	val instances = graph.members.filterIsInstance(dependencyClass)
	check(instances.isNotEmpty()) { "$dependencyClass was not in graph" }
	requireNotNull(graph[this@dependsOnClass]) { "${this@dependsOnClass} was not in graph" }.plus(instances)
}

/**
 * all [NODE]s of type [dependantClass] depends on [this]
 *
 * will crash if no [NODE]s of type [dependantClass] exist in the graph
 */
fun <NODE: Any> NODE.dependedOnClass(dependantClass: Class<NODE>) = AdjacencyRule<NODE> { graph ->
	checkNotNull(graph[this@dependedOnClass]) { "${this@dependedOnClass} was not in graph" }
	val instances = graph.members.filterIsInstance(dependantClass)
	require(instances.isNotEmpty()) { "$dependantClass was not in graph" }
	instances.forEach { graph[it] + this@dependedOnClass }
}

/**
 * [this] depends on all [NODE]s of type [dependencyClass]
 */
fun <NODE: Any> NODE.optionalDependsOnClass(dependencyClass: Class<NODE>) = AdjacencyRule<NODE> { graph ->
	requireNotNull(graph[this@optionalDependsOnClass]) { "${this@optionalDependsOnClass} was not in graph" } +
			graph.members.filterIsInstance(dependencyClass)
}

/**
 * all [NODE]s of type [dependantClass] depends on [this]
 */
fun <NODE: Any> NODE.optionalDependedOnClass(dependantClass: Class<NODE>) = AdjacencyRule<NODE> { graph ->
	checkNotNull(graph[this@optionalDependedOnClass]) { "${this@optionalDependedOnClass} was not in graph" }
	graph.members.filterIsInstance(dependantClass).forEach { graph[it] + this@optionalDependedOnClass }
}
