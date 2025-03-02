package dev.frozenmilk.util.cell

import dev.frozenmilk.util.observe.Observable
import dev.frozenmilk.util.observe.Observer
import dev.frozenmilk.util.observe.Observerable

open class RefCell<T> (protected var ref: T) : CellBase<T>(), Observerable<T> {
	override fun get(): T {
		lastGet = System.nanoTime()
		return ref
	}

	private fun accept(source: Observable<*>, p0: T) {
		lastSet = System.nanoTime()
		ref = p0
		observers.forEach {
			it.update(source, ref)
		}
	}

	override fun accept(p0: T) = accept(this, p0)

	private val observers = mutableSetOf<Observer<in T>>()

	override fun bind(observer: Observer<in T>) {
		if (observer == this) throw IllegalArgumentException("Self binding is illegal")
		observers.add(observer)
		observer.update(this, ref)
	}

	override fun unbind(observer: Observer<in T>) = observers.remove(observer)

	override fun update(source: Observable<*>, new: T) {
		if (source != this) accept(source, new) // ensures no cyclic operations
	}

	override fun toString() = "${javaClass.simpleName}|$ref|"
}