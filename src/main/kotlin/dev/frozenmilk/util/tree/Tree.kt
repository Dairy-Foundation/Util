package dev.frozenmilk.util.tree

import java.util.function.Consumer
import java.util.function.Function

open class Tree<K, V> (var contents: V) {
	protected val children= mutableMapOf<K, Tree<K, V>>()
	operator fun get(vararg keys: K) = get(keys.toList())
	operator fun get(key: K): V? = children[key]?.contents

	operator fun get(keys: Collection<K>): V? {
		val k = keys.firstOrNull() ?: return contents
		return (children[k] ?: return null)[keys.drop(1)]
	}
	operator fun set(key: K, value: V) {
		children[key]?.contents = value
	}

	/**
	 * returns the node at the end of the key collection
	 *
	 * empty nodes are set lazily via [orElse]
	 */
	fun getOrElse(keys: Collection<K>, orElse: Function<in K, out V>) : Tree<K, V> {
		var treeWalker = this
		keys.forEach {
			treeWalker.computeIfAbsent(it, orElse)
			treeWalker = treeWalker.children[it]!!
		}
		return treeWalker
	}

	/**
	 * returns the node at the end of the key collection
	 *
	 * empty nodes are set to [default]
	 */
	fun getOrDefault(keys: Collection<K>, default: V) : Tree<K, V> {
		var treeWalker = this
		keys.forEach {
			treeWalker.putIfAbsent(it, default)
			treeWalker = treeWalker.children[it]!!
		}
		return treeWalker
	}

	fun containsKey(key: K): Boolean = children.containsKey(key)
	fun computeIfAbsent(key: K, orElse: Function<in K, out V>) = children.computeIfAbsent(key) { Tree(orElse.apply(key)) }
	fun putIfAbsent(key: K, value: V) = children.putIfAbsent(key, Tree(value))
	fun forEach(f: Consumer<Tree<K, V>>) {
		f.accept(this)
		children.forEach { it.value.forEach(f) }
	}

	/**
	 * in place replaces this with [other]
	 */
	fun clone(other: Tree<K, V>) {
		contents = other.contents
		children.clear()
		children.putAll(other.children)
	}
	operator fun set(key: K, subtree: Tree<K, V>) {
		children[key] = subtree
	}
}