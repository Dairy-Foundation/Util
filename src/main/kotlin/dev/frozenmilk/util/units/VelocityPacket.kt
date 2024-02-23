package dev.frozenmilk.util.units

/**
 * a more internal representation of velocity, that stores the requisite information for calculating velocity, but doesn't define the operation
 */
data class VelocityPacket<T> (
	val start: T,
	val end: T,
	/**
	 * in seconds
	 */
	val startTime: Double,
	/**
	 * in seconds
	 */
	val endTime: Double,
){
	/**
	 * in seconds
	 */
	val deltaTime = endTime - startTime
}

// internal helper methods for finding the velocity
fun VelocityPacket<out Number>.getVelocity() = (end.toDouble() - start.toDouble()) / deltaTime
fun <U: Unit<U>, RU: ReifiedUnit<U, RU>> VelocityPacket<out RU>.getVelocity() = (start.findError(end)) / deltaTime

fun <T> Collection<VelocityPacket<out T>>.homogenise() = VelocityPacket(first().start, last().end, first().startTime, last().endTime)