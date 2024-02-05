package dev.frozenmilk.util.units

import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.pow

/**
 * common value is radians
 */
interface AngleUnit : Unit<AngleUnit> {
	val wrapAt: Double
	fun absolute(value: Double) = (value % wrapAt).run {
		if (this < 0) this + wrapAt
		else this
	}

	val linearAngleUnit: LinearAngleUnit
}

enum class AngleUnits(override val toCommonRatio: Double, override val wrapAt: Double, override val linearAngleUnit: LinearAngleUnit) : AngleUnit {
	RADIAN(1.0, Math.PI * 2, LinearAngleUnits.RADIAN),
	DEGREE(Math.PI / 180.0, 360.0, LinearAngleUnits.DEGREE),
}

class Angle(val angleUnit: AngleUnit = AngleUnits.RADIAN, value: Double = 0.0, power: Int = 1) : ReifiedUnit<AngleUnit, Angle>(angleUnit.absolute(value), power) {
	override fun into(unit: AngleUnit) = Angle(unit, angleUnit.into(unit, value, power), power)
	fun into(unit: LinearAngleUnit) = LinearAngle(unit, angleUnit.linearAngleUnit.into(unit, value, power), power)
	override fun plus(reifiedUnit: ReifiedUnit<AngleUnit, Angle>) =
			if (power == reifiedUnit.power) Angle(angleUnit, value + reifiedUnit.into(angleUnit).value, power)
			else throw UnsupportedOperationException("Cannot add Angles with different powers.")
	override fun minus(reifiedUnit: ReifiedUnit<AngleUnit, Angle>) =
			if (power == reifiedUnit.power) Angle(angleUnit, value - reifiedUnit.into(angleUnit).value, power)
			else throw UnsupportedOperationException("Cannot subtract Angles with different powers.")
	override fun unaryPlus() = this
	override fun unaryMinus() = Angle(angleUnit, -value, power)
	override fun times(multiplier: Double) = Angle(angleUnit, value * multiplier, power)
	override fun times(reifiedUnit: ReifiedUnit<AngleUnit, Angle>) = Angle(angleUnit, value * reifiedUnit.into(angleUnit).value, power + reifiedUnit.power)
	override fun div(divisor: Double) = Angle(angleUnit, value / divisor, power)
	override fun div(reifiedUnit: ReifiedUnit<AngleUnit, Angle>) = Angle(angleUnit, value / reifiedUnit.into(angleUnit).value, power - reifiedUnit.power)
	override fun findShortestDistance(reifiedUnit: ReifiedUnit<AngleUnit, Angle>): ReifiedUnit<AngleUnit, Angle> {
		if (power != 1 || reifiedUnit.power != 1) throw UnsupportedOperationException("Undefined shortest distance between Angles with power != 1")
		val difference: Double = (reifiedUnit.into(angleUnit).value - this.value) % (angleUnit.wrapAt)
		if (difference > (angleUnit.wrapAt / 2.0)) {
			return Angle(angleUnit, -angleUnit.wrapAt + difference, power)
		}
		if (difference < -(angleUnit.wrapAt / 2.0)) {
			return Angle(angleUnit, angleUnit.wrapAt + difference, power)
		}
		return Angle(angleUnit, difference, power)
	}

	override fun abs() = Angle(angleUnit, abs(value), power)
	override fun coerceAtLeast(minimumValue: ReifiedUnit<AngleUnit, Angle>) =
			if (power == minimumValue.power) Angle(angleUnit, value.coerceAtLeast(minimumValue.into(angleUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce an Angle with another of a different power")
	override fun coerceAtMost(maximumValue: ReifiedUnit<AngleUnit, Angle>) =
			if (power == maximumValue.power) Angle(angleUnit, value.coerceAtMost(maximumValue.into(angleUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce an Angle with another of a different power")
	override fun coerceIn(minimumValue: ReifiedUnit<AngleUnit, Angle>, maximumValue: ReifiedUnit<AngleUnit, Angle>) =
			if (power == minimumValue.power && power == maximumValue.power)
				Angle(angleUnit, value.coerceIn(minimumValue.into(angleUnit).value, maximumValue.into(angleUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce an Angle with another of a different power")
	override fun pow(n: Int) = Angle(angleUnit, value.pow(n), power * n)

	override fun toString() = "$value $angleUnit $power"
	override fun equals(other: Any?) = other is Angle && abs((this - other).value) < 1e-12 && power == other.power
	override fun hashCode(): Int = into(AngleUnits.RADIAN).value.hashCode() // ignores power
	override fun compareTo(other: ReifiedUnit<AngleUnit, Angle>) = value.compareTo(other.into(angleUnit).value) // ignores power

	companion object {
		@JvmStatic
		val NEGATIVE_INFINITY: Angle = Angle(value = Double.NEGATIVE_INFINITY)
		@JvmStatic
		val POSITIVE_INFINITY: Angle = Angle(value = Double.POSITIVE_INFINITY)
		@JvmStatic
		val NaN: Angle = Angle(value = Double.NaN)
	}

	// quick intos
	fun intoDegrees() = into(AngleUnits.DEGREE)
	fun intoRadians() = into(AngleUnits.RADIAN)
}

fun Supplier<out Angle>.into(unit: AngleUnit) = Supplier<Angle> { get().into(unit) }
fun Supplier<out Angle>.intoRadians() = Supplier<Angle> { get().intoRadians() }
fun Supplier<out Angle>.intoDegrees() = Supplier<Angle> { get().intoDegrees() }
