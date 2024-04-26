package dev.frozenmilk.util.units.position

import dev.frozenmilk.util.units.distance.DistanceUnit
import dev.frozenmilk.util.units.distance.DistanceUnits
import dev.frozenmilk.util.units.angle.Angle
import dev.frozenmilk.util.units.angle.AngleUnit
import dev.frozenmilk.util.units.angle.AngleUnits
import dev.frozenmilk.util.units.angle.Wrapping
import java.util.Objects

class Pose2D @JvmOverloads constructor(val vector2D: Vector2D = Vector2D(), val heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) {
	/**
	 * non-mutating
	 */
	fun into(xUnit: DistanceUnit, yUnit: DistanceUnit, headingUnit: AngleUnit, headingWrapping: Wrapping) = Pose2D(vector2D.into(xUnit, yUnit), heading.into(headingUnit).into(headingWrapping))
	/**
	 * non-mutating
	 */
	fun into(xUnit: DistanceUnit, yUnit: DistanceUnit = xUnit) = Pose2D(vector2D.into(xUnit, yUnit), heading)
	/**
	 * non-mutating
	 */
	fun into(headingUnit: AngleUnit, headingWrapping: Wrapping = heading.wrapping) = Pose2D(vector2D, heading.into(headingUnit).into(headingWrapping))
	/**
	 * non-mutating
	 */
	fun into(headingWrapping: Wrapping) = Pose2D(vector2D, heading.into(headingWrapping))

	/**
	 * non-mutating
	 */
	operator fun plus(pose2D: Pose2D) = Pose2D(vector2D + pose2D.vector2D, heading + pose2D.heading)

	/**
	 * non-mutating
	 */
	operator fun plus(vector2D: Vector2D) = Pose2D(this.vector2D + vector2D, heading)

	/**
	 * non-mutating
	 */
	operator fun plus(heading: Angle) = Pose2D(vector2D, this.heading + heading)

	/**
	 * non-mutating
	 */
	operator fun minus(pose2D: Pose2D) = Pose2D(this.vector2D - pose2D.vector2D, this.heading - pose2D.heading)

	/**
	 * non-mutating
	 */
	operator fun minus(vector2D: Vector2D) = Pose2D(this.vector2D - vector2D, heading)

	/**
	 * non-mutating
	 *
	 * has no effect
	 */
	operator fun unaryPlus() = this

	/**
	 * non-mutating
	 *
	 * equivalent of rotating the vector 180 degrees, and rotating the heading 180 degrees
	 */
	operator fun unaryMinus() = Pose2D(-vector2D, -heading)

	override fun toString() = "$vector2D, $heading"

	override fun equals(other: Any?) = other is Pose2D && vector2D == other.vector2D && heading == other.heading

	override fun hashCode() = Objects.hash(vector2D, heading)

	// quick intos (vector)
	fun intoMillimeters() = into(DistanceUnits.MILLIMETER)
	fun intoInches() = into(DistanceUnits.INCH)
	fun intoFeet() = into(DistanceUnits.FOOT)
	fun intoMeters() = into(DistanceUnits.METER)
	// quick intos (heading)
	fun intoDegrees() = into(AngleUnits.DEGREE)
	fun intoRadians() = into(AngleUnits.RADIAN)
	fun intoWrapping() = into(Wrapping.WRAPPING)
	fun intoLinear() = into(Wrapping.LINEAR)

}

fun millimeterPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(millimeterVector(x, y), heading)
fun inchPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(inchVector(x, y), heading)
fun meterPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(meterVector(x, y), heading)
fun footPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(footVector(x, y), heading)

fun millimeterPose(vector2D: Vector2D = millimeterVector(), heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(vector2D.into(DistanceUnits.MILLIMETER), heading)
fun inchPose(vector2D: Vector2D = inchVector(), heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(vector2D.into(DistanceUnits.INCH), heading)
fun meterPose(vector2D: Vector2D = meterVector(), heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(vector2D.into(DistanceUnits.MILLIMETER), heading)
fun footPose(vector2D: Vector2D = footVector(), heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = Pose2D(vector2D.into(DistanceUnits.MILLIMETER), heading)
