@file:JvmName("DistancePose2Ds")
package dev.frozenmilk.util.units.position

import dev.frozenmilk.util.units.distance.DistanceUnit
import dev.frozenmilk.util.units.distance.DistanceUnits
import dev.frozenmilk.util.units.angle.Angle
import dev.frozenmilk.util.units.angle.AngleUnit
import dev.frozenmilk.util.units.angle.AngleUnits
import dev.frozenmilk.util.units.angle.Wrapping
import dev.frozenmilk.util.units.distance.Distance
import java.util.Objects

class DistancePose2D
@JvmOverloads
constructor(
	override val vector2D: DistanceVector2D = DistanceVector2D(),
	override val heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)
) : Pose2D<Distance, DistanceVector2D, DistancePose2D>() {
	fun into(xUnit: DistanceUnit, yUnit: DistanceUnit, headingUnit: AngleUnit, headingWrapping: Wrapping) = if (xUnit == vector2D.x.unit && yUnit == vector2D.y.unit && heading.unit == headingUnit && heading.wrapping == headingWrapping) this else DistancePose2D(vector2D.into(xUnit, yUnit), heading.into(headingUnit).into(headingWrapping))
	@JvmOverloads
	fun into(xUnit: DistanceUnit, yUnit: DistanceUnit = xUnit) = if (xUnit == vector2D.x.unit && yUnit == vector2D.y.unit) this else DistancePose2D(vector2D.into(xUnit, yUnit), heading)
	override fun into(headingUnit: AngleUnit, headingWrapping: Wrapping) = if (heading.unit == headingUnit && heading.wrapping == headingWrapping) this else DistancePose2D(vector2D, heading.into(headingUnit, headingWrapping))
	override fun into(headingUnit: AngleUnit) = if (heading.unit == headingUnit) this else DistancePose2D(vector2D, heading.into(headingUnit))
	override fun into(headingWrapping: Wrapping) = if (heading.wrapping == headingWrapping) this else DistancePose2D(vector2D, heading.into(headingWrapping))
	override operator fun plus(pose2D: DistancePose2D) = if (pose2D.vector2D.x.value == 0.0 && pose2D.vector2D.y.value == 0.0 && pose2D.heading.value == 0.0) this else DistancePose2D(vector2D + pose2D.vector2D, heading + pose2D.heading)
	override operator fun plus(vector2D: DistanceVector2D) = if (vector2D.x.value == 0.0 && vector2D.y.value == 0.0) this else DistancePose2D(this.vector2D + vector2D, heading)
	override operator fun plus(heading: Angle) = if (heading.value == 0.0) this else DistancePose2D(vector2D, this.heading + heading)
	override operator fun minus(pose2D: DistancePose2D) = if (pose2D.vector2D.x.value == 0.0 && pose2D.vector2D.y.value == 0.0 && pose2D.heading.value == 0.0) this else DistancePose2D(this.vector2D - pose2D.vector2D, this.heading - pose2D.heading)
	override operator fun minus(vector2D: DistanceVector2D) = if (vector2D.x.value == 0.0 && vector2D.y.value == 0.0) this else DistancePose2D(this.vector2D - vector2D, heading)
	override operator fun minus(heading: Angle) = if (heading.value == 0.0) this else DistancePose2D(vector2D, this.heading - heading)
	override fun times(scalar: Double) = if (scalar == 1.0) this else DistancePose2D(vector2D * scalar, heading * scalar)
	override fun div(scalar: Double) = if (scalar == 1.0) this else DistancePose2D(vector2D / scalar, heading / scalar)
	override operator fun unaryPlus() = this
	override operator fun unaryMinus() = DistancePose2D(-vector2D, -heading)
	override fun toString() = "$vector2D, $heading"
	override fun equals(other: Any?) = other is DistancePose2D && vector2D == other.vector2D && heading == other.heading
	override fun hashCode() = Objects.hash(vector2D, heading)
	// quick intos (vector)
	fun intoMillimeters() = into(DistanceUnits.MILLIMETER)
	fun intoCentimeters() = into(DistanceUnits.CENTIMETER)
	fun intoInches() = into(DistanceUnits.INCH)
	fun intoFeet() = into(DistanceUnits.FOOT)
	fun intoMeters() = into(DistanceUnits.METER)
}

@JvmOverloads
fun millimeterPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = DistancePose2D(millimeterVector(x, y), heading)
@JvmOverloads
fun centimeterPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = DistancePose2D(centimeterVector(x, y), heading)
@JvmOverloads
fun meterPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = DistancePose2D(meterVector(x, y), heading)
@JvmOverloads
fun inchPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = DistancePose2D(inchVector(x, y), heading)
@JvmOverloads
fun footPose(x: Double = 0.0, y: Double = 0.0, heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)) = DistancePose2D(footVector(x, y), heading)
