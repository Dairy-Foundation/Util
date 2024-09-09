package dev.frozenmilk.util.units.position

import dev.frozenmilk.util.units.angle.Angle

abstract class Vector2D<T, SELF: Vector2D<T, SELF>> {
	abstract val x: T
	abstract val y: T

	/**
	 * angle of the vector, always a [Angle] of type [dev.frozenmilk.util.units.angle.AngleUnits.RADIAN] under the hood
	 */
	abstract val theta: Angle

	/**
	 * length of the vector
	 */
	abstract val magnitude: T
	operator fun component1(): T = x
	operator fun component2(): T = y

	/**
	 * non-mutating
	 */
	abstract operator fun plus(vector2D: SELF): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun minus(vector2D: SELF): SELF

	/**
	 * non-mutating
	 *
	 * has no effect
	 */
	abstract operator fun unaryPlus(): SELF

	/**
	 * non-mutating
	 *
	 * equivalent to [rotate] 180 degrees
	 *
	 * also equivalent to [times] -1.0
	 */
	abstract operator fun unaryMinus(): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun times(scalar: T): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun div(scalar: T): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun times(scalar: Double): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun div(scalar: Double): SELF

	/**
	 * non-mutating
	 */
	abstract infix fun rotate(angle: Angle): SELF

	/**
	 * non-mutating
	 */
	abstract fun normalise(length: T): SELF
	/**
	 * non-mutating
	 */
	abstract fun normalise(): SELF

	/**
	 * non-mutating
	 */
	abstract infix fun dot(vector2D: SELF): T

	abstract override fun toString(): String

	abstract override fun equals(other: Any?): Boolean

	abstract override fun hashCode(): Int
}