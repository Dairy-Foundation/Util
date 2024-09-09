package dev.frozenmilk.util.units.position

import dev.frozenmilk.util.units.angle.Angle
import dev.frozenmilk.util.units.angle.AngleUnit
import dev.frozenmilk.util.units.angle.AngleUnits
import dev.frozenmilk.util.units.angle.Wrapping
import java.util.Objects

class DoublePose2D
@JvmOverloads
constructor(
	override val vector2D: DoubleVector2D = DoubleVector2D(),
	override val heading: Angle = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING)
) : Pose2D<Double, DoubleVector2D, DoublePose2D>() {
	override fun into(headingUnit: AngleUnit, headingWrapping: Wrapping) = if (heading.unit == headingUnit && heading.wrapping == headingWrapping) this else DoublePose2D(vector2D, heading.into(headingUnit, headingWrapping))
	override fun into(headingUnit: AngleUnit) = if (heading.unit == headingUnit) this else DoublePose2D(vector2D, heading.into(headingUnit))
	override fun into(headingWrapping: Wrapping) = if (heading.wrapping == headingWrapping) this else DoublePose2D(vector2D, heading.into(headingWrapping))
	override operator fun plus(pose2D: DoublePose2D) = if (pose2D.vector2D.x == 0.0 && pose2D.vector2D.y == 0.0 && pose2D.heading.value == 0.0) this else DoublePose2D(vector2D + pose2D.vector2D, heading + pose2D.heading)
	override operator fun plus(vector2D: DoubleVector2D) = if (vector2D.x == 0.0 && vector2D.y == 0.0) this else DoublePose2D(this.vector2D + vector2D, heading)
	override operator fun plus(heading: Angle) = if (heading.value == 0.0) this else DoublePose2D(vector2D, this.heading + heading)
	override operator fun minus(pose2D: DoublePose2D) = if (pose2D.vector2D.x == 0.0 && pose2D.vector2D.y == 0.0 && pose2D.heading.value == 0.0) this else DoublePose2D(this.vector2D - pose2D.vector2D, this.heading - pose2D.heading)
	override operator fun minus(vector2D: DoubleVector2D) =  if (vector2D.x == 0.0 && vector2D.y == 0.0) this else DoublePose2D(this.vector2D - vector2D, heading)
	override operator fun minus(heading: Angle) = if (heading.value == 0.0) this else DoublePose2D(vector2D, this.heading - heading)
	override fun times(scalar: Double) = if (scalar == 1.0) this else DoublePose2D(vector2D * scalar, heading * scalar)
	override fun div(scalar: Double) = if (scalar == 1.0) this else DoublePose2D(vector2D / scalar, heading / scalar)
	override operator fun unaryPlus() = this
	override operator fun unaryMinus() = DoublePose2D(-vector2D, -heading)
	override fun toString() = "$vector2D, $heading"
	override fun equals(other: Any?) = other is DoublePose2D && vector2D == other.vector2D && heading == other.heading
	override fun hashCode() = Objects.hash(vector2D, heading)
}
