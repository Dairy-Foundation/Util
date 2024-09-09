package dev.frozenmilk.util.observe

@FunctionalInterface
fun interface Observable<T> {
	/**
	 * binds [observer] to this
	 *
	 * then calls [Observer.update]
	 */
	fun bind(observer: Observer<in T>)
}