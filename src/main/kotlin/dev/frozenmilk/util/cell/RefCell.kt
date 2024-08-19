package dev.frozenmilk.util.cell

import dev.frozenmilk.util.observe.Observer
import dev.frozenmilk.util.observe.Observerable
import java.lang.ref.WeakReference

open class RefCell<T> (protected var ref: T) : CellBase<T>(), Observerable<T> {
	override fun get(): T {
		lastGet = System.nanoTime()
		return ref
	}
	override fun accept(p0: T) {
		lastSet = System.nanoTime()
		ref = p0
		observers.forEach {
			it.get()?.update(ref)
		}
	}

	protected val observers = mutableSetOf<WeakReference<Observer<in T>>>()

	override fun bind(observer: Observer<in T>) {
		if (observer == this) throw IllegalArgumentException("Self binding is illegal")
		observers.removeIf { it.get() == null }
		observers.add(WeakReference(observer))
		observer.update(ref)
	}

	override fun update(new: T) {
		if (new != ref) accept(new) // ensures no cyclic operations
	}

	override fun toString() = "${javaClass.simpleName}|$ref|"
}