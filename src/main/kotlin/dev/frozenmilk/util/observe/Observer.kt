package dev.frozenmilk.util.observe

@FunctionalInterface
fun interface Observer<T> {
	fun update(new: T)
}