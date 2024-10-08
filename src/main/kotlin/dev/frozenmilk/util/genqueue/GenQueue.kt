package dev.frozenmilk.util.genqueue

import java.util.function.Function

/**
 * assumes that all values will be returned to this instance via [giveBack]
 *
 * @param first the first value produced by this
 * @param generator the generator function to generate the next value of the queue from the previously generated value,
 * starting with [first]. If it throws any exception, the generator will be marked as finished, and
 */
class GenQueue<T>(first: T, private val generator: Function<T, T>) {
	private var prev = first
	private var generating = true
	private val queue = ArrayDeque(listOf(first))
	fun next(): T {
		if (queue.isEmpty()) {
			if (generating) {
				try {
					return generator.apply(prev).also { prev = it } // generate next, set prev to it
				} catch (_: Throwable) {
					generating = false
				}
			}
			@Suppress("ControlFlowWithEmptyBody")
			while (queue.isEmpty()) {} // await
		}
		return queue.removeFirst()
	}

	/**
	 * should only be called with [element]s generated by this, adds [element] back to the queue
	 */
	fun giveBack(element: T) = queue.addLast(element)
}