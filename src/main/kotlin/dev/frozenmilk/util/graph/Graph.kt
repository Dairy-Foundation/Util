package dev.frozenmilk.util.graph

interface Graph<NODE: Any> {
	val size: Int

	/**
	 * the members of the graph
	 */
	val members: Set<NODE>

	/**
	 * returns the [DependencySet] for [dependent] if it exists
	 */
	operator fun get(dependent: NODE): DependencySet<NODE>

	interface DependencySet<NODE> {
		/**
		 * adds [dependency] to this
		 */
		operator fun plus(dependency: NODE): DependencySet<NODE>
		/**
		 * adds [dependencies] to this
		 */
		operator fun plus(dependencies: Collection<NODE>): DependencySet<NODE>
	}
}