package dev.frozenmilk.util.observe

@FunctionalInterface
fun interface Observable<T> {
	/**
	 * binds [observer] receive [Observer.update]s from this [Observable], then calls [Observer.update]
	 */
	fun bind(observer: Observer<in T>)
}