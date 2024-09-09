package dev.frozenmilk.util.units

import dev.frozenmilk.util.units.position.Pose2D
import dev.frozenmilk.util.units.position.Vector2D

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
fun <RU: ReifiedUnit<*, RU>> VelocityPacket<out RU>.getVelocity() = (start.findError(end)) / deltaTime
fun <VEC: Vector2D<*, VEC>> VelocityPacket<out VEC>.getVelocity() = (start - end) / deltaTime
fun <POSE: Pose2D<*, *, POSE>> VelocityPacket<out POSE>.getVelocity() = (start.intoLinear() - end.intoLinear()) / deltaTime
fun <T> Collection<VelocityPacket<out T>>.homogenise() = VelocityPacket(first().start, last().end, first().startTime, last().endTime)