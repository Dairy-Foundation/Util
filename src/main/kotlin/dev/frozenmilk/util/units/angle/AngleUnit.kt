package dev.frozenmilk.util.units.angle

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.abs

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

class Angle @JvmOverloads constructor(val angleUnit: AngleUnit, value: Double = 0.0) : ReifiedUnit<AngleUnit, Angle>(angleUnit.absolute(value)) {
	override fun into(unit: AngleUnit) = Angle(unit, angleUnit.into(unit, value))
	fun into(unit: LinearAngleUnit) = LinearAngle(unit, angleUnit.linearAngleUnit.into(unit, value))
	override fun plus(reifiedUnit: ReifiedUnit<AngleUnit, Angle>) = Angle(angleUnit, value + reifiedUnit.into(angleUnit).value)
	override fun minus(reifiedUnit: ReifiedUnit<AngleUnit, Angle>) = Angle(angleUnit, value - reifiedUnit.into(angleUnit).value)
	override fun unaryPlus() = this
	override fun unaryMinus() = Angle(angleUnit, -value)
	override fun times(multiplier: Double) = Angle(angleUnit, value * multiplier)
	override fun div(divisor: Double) = Angle(angleUnit, value / divisor)
	fun findShortestDistance(reifiedUnit: ReifiedUnit<AngleUnit, Angle>): ReifiedUnit<AngleUnit, Angle> {
		val difference: Double = (reifiedUnit.into(angleUnit).value - this.value) % (angleUnit.wrapAt)
		if (difference > (angleUnit.wrapAt / 2.0)) {
			return Angle(angleUnit, -angleUnit.wrapAt + difference)
		}
		if (difference < -(angleUnit.wrapAt / 2.0)) {
			return Angle(angleUnit, angleUnit.wrapAt + difference)
		}
		return Angle(angleUnit, difference)
	}

	override fun abs() = Angle(angleUnit, abs(value))
	override fun coerceAtLeast(minimumValue: ReifiedUnit<AngleUnit, Angle>) = Angle(angleUnit, value.coerceAtLeast(minimumValue.into(angleUnit).value))
	override fun coerceAtMost(maximumValue: ReifiedUnit<AngleUnit, Angle>) = Angle(angleUnit, value.coerceAtMost(maximumValue.into(angleUnit).value))
	override fun coerceIn(minimumValue: ReifiedUnit<AngleUnit, Angle>, maximumValue: ReifiedUnit<AngleUnit, Angle>) = Angle(angleUnit, value.coerceIn(minimumValue.into(angleUnit).value, maximumValue.into(angleUnit).value))
//	override fun pow(n: Int) = Angle(angleUnit, value.pow(n), power * n)

	override fun toString() = "$value $angleUnit"
	override fun equals(other: Any?) = other is Angle && abs((this - other).value) < 1e-12
	override fun hashCode(): Int = into(AngleUnits.RADIAN).value.hashCode() // ignores power
	override fun compareTo(other: ReifiedUnit<AngleUnit, Angle>) = value.compareTo(other.into(angleUnit).value) // ignores power

	companion object {
		@JvmStatic
		val NEGATIVE_INFINITY: Angle = Angle(AngleUnits.RADIAN, Double.NEGATIVE_INFINITY)
		@JvmStatic
		val POSITIVE_INFINITY: Angle = Angle(AngleUnits.RADIAN, Double.POSITIVE_INFINITY)
		@JvmStatic
		val NaN: Angle = Angle(AngleUnits.RADIAN, Double.NaN)
	}

	// quick intos
	fun intoDegrees() = into(AngleUnits.DEGREE)
	fun intoRadians() = into(AngleUnits.RADIAN)
}

fun Supplier<out Angle>.into(unit: AngleUnit) = Supplier<Angle> { get().into(unit) }
fun Supplier<out Angle>.intoRadians() = Supplier<Angle> { get().intoRadians() }
fun Supplier<out Angle>.intoDegrees() = Supplier<Angle> { get().intoDegrees() }
