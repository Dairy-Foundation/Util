package dev.frozenmilk.util.units.position

import dev.frozenmilk.util.units.angle.Angle
import dev.frozenmilk.util.units.angle.AngleUnits
import dev.frozenmilk.util.units.angle.Wrapping
import java.util.Objects
import kotlin.math.atan2
import kotlin.math.hypot

class DoubleVector2D @JvmOverloads constructor(override val x: Double = 0.0, override val y: Double = 0.0) : Vector2D<Double, DoubleVector2D>() {
	override val theta: Angle by lazy { Angle(AngleUnits.RADIAN, Wrapping.WRAPPING, atan2(y, x)) }
	override val magnitude: Double = hypot(x, y)
	/**
	 * polar constructor
	 */
	constructor(magnitude: Double, t: Angle) : this(magnitude, t.intoRadians().intoWrapping(), true)
	/**
	 * internal polar constructor
	 */
	private constructor(magnitude: Double, t: Angle, _f: Boolean) : this(magnitude * t.cos, magnitude * t.sin)
	override operator fun plus(vector2D: DoubleVector2D) = DoubleVector2D(x + vector2D.x, y + vector2D.y)
	override operator fun minus(vector2D: DoubleVector2D) = DoubleVector2D(x - vector2D.x, y - vector2D.y)
	override operator fun unaryPlus(): DoubleVector2D = this
	override operator fun unaryMinus() = this * -1.0
	override operator fun times(scalar: Double) = DoubleVector2D(x * scalar, y * scalar)
	override operator fun div(scalar: Double) = DoubleVector2D(x / scalar, y / scalar)
	override infix fun rotate(angle: Angle): DoubleVector2D {
		@Suppress("NAME_SHADOWING")
		val angle = angle.intoRadians().intoWrapping()
		return DoubleVector2D(x * angle.cos - y * angle.sin, x * angle.sin + y * angle.cos)
	}
	override fun normalise(length: Double) = DoubleVector2D(length, theta)
	override fun normalise() = normalise(1.0)
	override infix fun dot(vector2D: DoubleVector2D) = x * vector2D.x + y * vector2D.y
	override fun toString(): String = "($x, $y)"
	override fun equals(other: Any?): Boolean = other is DoubleVector2D && this.x == other.x && this.y == other.y
	override fun hashCode(): Int = Objects.hash(x, y)
}