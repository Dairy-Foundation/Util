@file:JvmName("DistanceVector2Ds")
package dev.frozenmilk.util.units.position

import dev.frozenmilk.util.units.angle.Angle
import dev.frozenmilk.util.units.angle.AngleUnits
import dev.frozenmilk.util.units.angle.Wrapping
import dev.frozenmilk.util.units.distance.Distance
import dev.frozenmilk.util.units.distance.DistanceUnit
import dev.frozenmilk.util.units.distance.DistanceUnits
import dev.frozenmilk.util.units.distance.mm
import java.util.Objects
import kotlin.math.atan2
import kotlin.math.hypot

class DistanceVector2D
@JvmOverloads
constructor(
	override val x: Distance = Distance(DistanceUnits.MILLIMETER, 0.0),
	override val y: Distance = Distance(DistanceUnits.MILLIMETER, 0.0)
) : Vector2D<Distance, DistanceVector2D>() {
	override val theta: Angle by lazy { Angle(AngleUnits.RADIAN, Wrapping.WRAPPING, atan2(y.intoMillimeters().value, x.intoMillimeters().value)) }

	/**
	 * length of the vector, always a [Distance] of type [DistanceUnits.MILLIMETER] under the hood
	 */
	override val magnitude: Distance by lazy { Distance(DistanceUnits.MILLIMETER, hypot(x.intoMillimeters().value, y.intoMillimeters().value)) }
	constructor(distanceUnit: DistanceUnit = DistanceUnits.MILLIMETER, x: Double = 0.0, y: Double = 0.0) : this(Distance(distanceUnit, x), Distance(distanceUnit, y))

	/**
	 * polar constructor
	 */
	constructor(magnitude: Distance, t: Angle) : this(magnitude, t.intoRadians().intoWrapping(), true)
	/**
	 * internal polar constructor
	 */
	private constructor(magnitude: Distance, t: Angle, _f: Boolean) : this(magnitude * t.cos, magnitude * t.sin)
	/**
	 * non-mutating
	 */
	@JvmOverloads
	fun into(xUnit: DistanceUnit, yUnit: DistanceUnit = xUnit) = DistanceVector2D(this.x.into(xUnit), this.y.into(yUnit))
	override operator fun plus(vector2D: DistanceVector2D) = DistanceVector2D(x + vector2D.x, y + vector2D.y)
	override operator fun minus(vector2D: DistanceVector2D) = DistanceVector2D(x - vector2D.x, y - vector2D.y)
	override operator fun unaryPlus(): DistanceVector2D = this
	override operator fun unaryMinus() = this * -1.0
	override operator fun times(scalar: Distance) = DistanceVector2D(x * scalar, y * scalar)
	override operator fun div(scalar: Distance) = DistanceVector2D(x / scalar, y / scalar)
	override operator fun times(scalar: Double) = DistanceVector2D(x * scalar, y * scalar)
	override operator fun div(scalar: Double) = DistanceVector2D(x / scalar, y / scalar)
	override infix fun rotate(angle: Angle): DistanceVector2D {
		@Suppress("NAME_SHADOWING")
		val angle = angle.intoRadians().intoWrapping()
		return DistanceVector2D(x * angle.cos - y * angle.sin, x * angle.sin + y * angle.cos)
	}
	override fun normalise(length: Distance) = DistanceVector2D(length, theta)
	override fun normalise() = normalise(1.mm)
	override infix fun dot(vector2D: DistanceVector2D) = x * vector2D.x + y * vector2D.y
	override fun toString(): String = "($x, $y)"
	override fun equals(other: Any?): Boolean = other is DistanceVector2D && this.x == other.x && this.y == other.y
	override fun hashCode(): Int = Objects.hash(x, y)

	// quick intos
	fun intoMillimeters() = into(DistanceUnits.MILLIMETER)
	fun intoCentimeters() = into(DistanceUnits.CENTIMETER)
	fun intoInches() = into(DistanceUnits.INCH)
	fun intoFeet() = into(DistanceUnits.FOOT)
	fun intoMeters() = into(DistanceUnits.METER)
}

@JvmOverloads
fun millimeterVector(x: Double = 0.0, y: Double = 0.0) = DistanceVector2D(DistanceUnits.MILLIMETER, x, y)
@JvmOverloads
fun centimeterVector(x: Double = 0.0, y: Double = 0.0) = DistanceVector2D(DistanceUnits.CENTIMETER, x, y)
@JvmOverloads
fun inchVector(x: Double = 0.0, y: Double = 0.0) = DistanceVector2D(DistanceUnits.INCH, x, y)
@JvmOverloads
fun meterVector(x: Double = 0.0, y: Double = 0.0) = DistanceVector2D(DistanceUnits.METER, x, y)
@JvmOverloads
fun footVector(x: Double = 0.0, y: Double = 0.0) = DistanceVector2D(DistanceUnits.FOOT, x, y)
