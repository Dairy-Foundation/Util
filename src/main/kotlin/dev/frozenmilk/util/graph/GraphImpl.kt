package dev.frozenmilk.util.graph

class GraphImpl<NODE: Any> @JvmOverloads constructor(val map: MutableMap<NODE, DependencySetImpl> = mutableMapOf()) : Graph<NODE> {
	fun initForSet(set: Set<NODE>) {
		map.keys.removeAll(map.keys - set)
		set.forEach {
			map.getOrPut(it) { DependencySetImpl(mutableSetOf()) }.set.clear()
		}
	}
	fun initForMap(map: Map<NODE, MutableSet<NODE>>) {
		this.map.keys.removeAll(this.map.keys - map.keys)
		map.forEach { (node, dependencies) -> this.map[node] = DependencySetImpl(dependencies) }
	}
	constructor(set: Set<NODE>): this(mutableMapOf<NODE, DependencySetImpl>()) {
		initForSet(set)
	}

	override val size = map.size

	/**
	 * the members of the graph
	 */
	override val members = map.keys as Set<NODE>

	/**
	 * returns the [DependencySet] for [dependent] if it exists
	 */
	override operator fun get(dependent: NODE) = map[dependent] as Graph.DependencySet<NODE>

	inner class DependencySetImpl(val set: MutableSet<NODE>): Graph.DependencySet<NODE> {
		/**
		 * adds [dependency] to this
		 */
		override operator fun plus(dependency: NODE) = apply {
			set.add(dependency)
		}
		/**
		 * adds [dependencies] to this
		 */
		override operator fun plus(dependencies: Collection<NODE>) = apply {
			set.addAll(dependencies)
		}
	}
}