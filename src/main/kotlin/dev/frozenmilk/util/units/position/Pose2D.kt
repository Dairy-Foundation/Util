package dev.frozenmilk.util.units.position

import dev.frozenmilk.util.units.angle.Angle
import dev.frozenmilk.util.units.angle.AngleUnit
import dev.frozenmilk.util.units.angle.AngleUnits
import dev.frozenmilk.util.units.angle.Wrapping

abstract class Pose2D<T, VEC: Vector2D<T, VEC>, SELF: Pose2D<T, VEC, SELF>> {
	abstract val vector2D: VEC
	abstract val heading: Angle

	operator fun component1() = vector2D
	operator fun component2() = heading

	/**
	 * non-mutating
	 */
	abstract fun into(
		headingUnit: AngleUnit,
		headingWrapping: Wrapping
	): SELF

	abstract fun into(
		headingUnit: AngleUnit
	): SELF

	/**
	 * non-mutating
	 */
	abstract fun into(headingWrapping: Wrapping): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun plus(pose2D: SELF): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun plus(vector2D: VEC): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun plus(heading: Angle): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun minus(pose2D: SELF): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun minus(vector2D: VEC): SELF

	/**
	 * non-mutating
	 */
	abstract operator fun minus(heading: Angle): SELF

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
	 *
	 * has no effect
	 */
	abstract operator fun unaryPlus(): SELF

	/**
	 * non-mutating
	 *
	 * equivalent of rotating the vector 180 degrees, and rotating the heading 180 degrees
	 */
	abstract operator fun unaryMinus(): SELF

	abstract override fun toString(): String

	abstract override fun equals(other: Any?): Boolean

	abstract override fun hashCode(): Int

	// quick intos (heading)
	fun intoDegrees() = into(AngleUnits.DEGREE)
	fun intoRadians() = into(AngleUnits.RADIAN)
	fun intoWrapping() = into(Wrapping.WRAPPING)
	fun intoRelative() = into(Wrapping.RELATIVE)
	fun intoLinear() = into(Wrapping.LINEAR)
}