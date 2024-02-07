package dev.frozenmilk.util.units.angle

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.abs

/**
 * common value is radians
 */
interface LinearAngleUnit : Unit<LinearAngleUnit> {
	val angleUnit: AngleUnit
}
enum class LinearAngleUnits(override val toCommonRatio: Double, override val angleUnit: AngleUnit) : LinearAngleUnit {
	RADIAN(1.0, AngleUnits.RADIAN),
	DEGREE(Math.PI / 180.0, AngleUnits.DEGREE);
}

class LinearAngle(val linearAngleUnit: LinearAngleUnit, value: Double = 0.0) : ReifiedUnit<LinearAngleUnit, LinearAngle>(value) {
	override fun into(unit: LinearAngleUnit) = LinearAngle(unit, linearAngleUnit.into(unit, value))
	fun into(unit: AngleUnit) = Angle(unit, linearAngleUnit.angleUnit.into(unit, value))
	override fun plus(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value + reifiedUnit.into(linearAngleUnit).value)
	override fun minus(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value - reifiedUnit.into(linearAngleUnit).value)
	override fun unaryPlus() = this
	override fun unaryMinus() = LinearAngle(linearAngleUnit, -value)
	override fun times(multiplier: Double) = LinearAngle(linearAngleUnit, value * multiplier)
//	override fun times(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value * reifiedUnit.into(linearAngleUnit).value, power + reifiedUnit.power)
	override fun div(divisor: Double) = LinearAngle(linearAngleUnit, value / divisor)
//	override fun div(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value / reifiedUnit.into(linearAngleUnit).value)
	fun findShortestDistance(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, reifiedUnit.into(linearAngleUnit).value - this.value)
	override fun abs() = LinearAngle(linearAngleUnit, abs(value))
	override fun coerceAtLeast(minimumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value.coerceAtLeast(minimumValue.into(linearAngleUnit).value))
	override fun coerceAtMost(maximumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value.coerceAtMost(maximumValue.into(linearAngleUnit).value))
	override fun coerceIn(minimumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>, maximumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value.coerceIn(minimumValue.into(linearAngleUnit).value, maximumValue.into(linearAngleUnit).value))
	override fun toString() = "$value $linearAngleUnit"
	override fun equals(other: Any?) = other is LinearAngle && abs((this - other).value) < 1e-12
	override fun hashCode(): Int = into(LinearAngleUnits.RADIAN).value.hashCode() // ignores power
	override fun compareTo(other: ReifiedUnit<LinearAngleUnit, LinearAngle>) = value.compareTo(other.into(linearAngleUnit).value) // ignores power

	companion object {
		@JvmStatic
		val NEGATIVE_INFINITY: LinearAngle = LinearAngle(LinearAngleUnits.RADIAN, Double.NEGATIVE_INFINITY)
		@JvmStatic
		val POSITIVE_INFINITY: LinearAngle = LinearAngle(LinearAngleUnits.RADIAN, Double.POSITIVE_INFINITY)
		@JvmStatic
		val NaN: LinearAngle = LinearAngle(LinearAngleUnits.RADIAN, Double.NaN)
	}

	// quick intos
	fun intoDegrees() = into(LinearAngleUnits.DEGREE)
	fun intoRadians() = into(LinearAngleUnits.RADIAN)
}

fun Supplier<out LinearAngle>.into(unit: LinearAngleUnit) = Supplier<LinearAngle> { get().into(unit) }
fun Supplier<out LinearAngle>.intoRadians() = Supplier<LinearAngle> { get().intoRadians() }
fun Supplier<out LinearAngle>.intoDegrees() = Supplier<LinearAngle> { get().intoDegrees() }
