package dev.frozenmilk.util.units

import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.pow

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

class LinearAngle(val linearAngleUnit: LinearAngleUnit = LinearAngleUnits.RADIAN, value: Double = 0.0, power: Int = 1) : ReifiedUnit<LinearAngleUnit, LinearAngle>(value, power) {
	override fun into(unit: LinearAngleUnit) = LinearAngle(unit, linearAngleUnit.into(unit, value, power), power)
	fun into(unit: AngleUnit) = Angle(unit, linearAngleUnit.angleUnit.into(unit, value, power), power)
	override fun plus(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) =
			if (power == reifiedUnit.power) LinearAngle(linearAngleUnit, value + reifiedUnit.into(linearAngleUnit).value, power)
			else throw UnsupportedOperationException("Cannot add LinearAngles with different powers.")
	override fun minus(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) =
			if (power == reifiedUnit.power) LinearAngle(linearAngleUnit, value - reifiedUnit.into(linearAngleUnit).value, power)
			else throw UnsupportedOperationException("Cannot subtract LinearAngles with different powers.")
	override fun unaryPlus() = this
	override fun unaryMinus() = LinearAngle(linearAngleUnit, -value, power)
	override fun times(multiplier: Double) = LinearAngle(linearAngleUnit, value * multiplier, power)
	override fun times(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value * reifiedUnit.into(linearAngleUnit).value, power + reifiedUnit.power)
	override fun div(divisor: Double) = LinearAngle(linearAngleUnit, value / divisor, power)
	override fun div(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>) = LinearAngle(linearAngleUnit, value / reifiedUnit.into(linearAngleUnit).value, power - reifiedUnit.power)
	override fun findShortestDistance(reifiedUnit: ReifiedUnit<LinearAngleUnit, LinearAngle>): ReifiedUnit<LinearAngleUnit, LinearAngle> {
		if (power == reifiedUnit.power) return LinearAngle(linearAngleUnit, reifiedUnit.into(linearAngleUnit).value - this.value, power)
		else throw UnsupportedOperationException("Cannot find shortest distance between LinearAngles with different powers")
	}

	override fun abs() = LinearAngle(linearAngleUnit, abs(value), power)
	override fun coerceAtLeast(minimumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>) =
			if (power == minimumValue.power) LinearAngle(linearAngleUnit, value.coerceAtLeast(minimumValue.into(linearAngleUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce a LinearAngle with another of a different power")
	override fun coerceAtMost(maximumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>) =
			if (power == maximumValue.power) LinearAngle(linearAngleUnit, value.coerceAtMost(maximumValue.into(linearAngleUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce a LinearAngle with another of a different power")
	override fun coerceIn(minimumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>, maximumValue: ReifiedUnit<LinearAngleUnit, LinearAngle>) =
			if (power == minimumValue.power && power == maximumValue.power)
				LinearAngle(linearAngleUnit, value.coerceIn(minimumValue.into(linearAngleUnit).value, maximumValue.into(linearAngleUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce a LinearAngle with another of a different power")
	override fun pow(n: Int) = LinearAngle(linearAngleUnit, value.pow(n), power * n)

	override fun toString() = "$value $linearAngleUnit $power"
	override fun equals(other: Any?) = other is LinearAngle && abs((this - other).value) < 1e-12 && power == other.power
	override fun hashCode(): Int = into(LinearAngleUnits.RADIAN).value.hashCode() // ignores power
	override fun compareTo(other: ReifiedUnit<LinearAngleUnit, LinearAngle>) = value.compareTo(other.into(linearAngleUnit).value) // ignores power

	companion object {
		val NEGATIVE_INFINITY: LinearAngle = LinearAngle(value = Double.NEGATIVE_INFINITY)
		val POSITIVE_INFINITY: LinearAngle = LinearAngle(value = Double.POSITIVE_INFINITY)
		val NaN: LinearAngle = LinearAngle(value = Double.NaN)
	}

	// quick intos
	fun intoDegrees() = into(LinearAngleUnits.DEGREE)
	fun intoRadians() = into(LinearAngleUnits.RADIAN)
}

fun Supplier<out LinearAngle>.into(unit: LinearAngleUnit) = Supplier<LinearAngle> { get().into(unit) }
fun Supplier<out LinearAngle>.intoRadians() = Supplier<LinearAngle> { get().intoRadians() }
fun Supplier<out LinearAngle>.intoDegrees() = Supplier<LinearAngle> { get().intoDegrees() }
