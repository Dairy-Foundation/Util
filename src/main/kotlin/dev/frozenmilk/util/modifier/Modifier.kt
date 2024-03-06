package dev.frozenmilk.util.modifier

@FunctionalInterface
fun interface Modifier<T> {
	fun modify(t: T): T
}