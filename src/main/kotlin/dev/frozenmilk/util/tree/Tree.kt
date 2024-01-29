package dev.frozenmilk.util.tree

open class Tree<K, V> private constructor(var parent: Tree<K, V>?, var contents: V) {
	constructor(contents: V) : this(null, contents)
	protected val children= mutableMapOf<K, Tree<K, V>>()
	operator fun get(key: K): Tree<K, V>? = children[key]
	operator fun get(keys: Collection<K>): V? = get(keys.toMutableList())
	operator fun get(keys: MutableList<K>): V? {
		val k = keys.firstOrNull() ?: return contents
		val tree = get(k) ?: return null
		keys.removeFirst()
		return tree[keys]
	}
	operator fun set(key: K, value: V) {
		children[key]?.contents = value
	}

	fun containsKey(key: K): Boolean = children.containsKey(key)

	fun putIfAbsent(key: K, value: V) = children.putIfAbsent(key, Tree(this, value))
}