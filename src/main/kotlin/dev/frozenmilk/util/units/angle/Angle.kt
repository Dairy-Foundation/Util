package dev.frozenmilk.util.units.angle

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * common value is radians
 */
interface AngleUnit : Unit<AngleUnit> {
	val wrapAt: Double
	fun mod(value: Double) = value.mod(wrapAt)
}

enum class AngleUnits(override val toCommonRatio: Double, override val wrapAt: Double) : AngleUnit {
	RADIAN(1.0, Math.PI * 2),
	DEGREE(Math.PI / 180.0, 360.0),
}

enum class Wrapping {
	WRAPPING,
	LINEAR
}

class Angle @JvmOverloads constructor(unit: AngleUnit, val wrapping: Wrapping, value: Double = 0.0) : ReifiedUnit<AngleUnit, Angle>(unit, unit.run { if(wrapping == Wrapping.WRAPPING) this.mod(value) else value }) {
	override fun into(unit: AngleUnit) = if (unit == this.unit) this else Angle(unit, wrapping, this.unit.into(unit, value))
	fun into(wrapping: Wrapping) = if (wrapping == this.wrapping) this else Angle(unit, wrapping, value)
	override fun plus(reifiedUnit: Angle) = Angle(unit, resultWrapping(reifiedUnit), value + reifiedUnit[unit])
	override fun minus(reifiedUnit: Angle) = Angle(unit, resultWrapping(reifiedUnit), value - reifiedUnit[unit])
	override fun unaryPlus() = this
	override fun unaryMinus() = Angle(unit, wrapping, -value)
	override fun times(multiplier: Double) = Angle(unit, wrapping, value * multiplier)
	override fun times(multiplier: Angle) = Angle(unit, resultWrapping(multiplier), value * multiplier[unit])
	override fun div(divisor: Double) = Angle(unit, wrapping, value / divisor)
	override fun div(divisor: Angle) = Angle(unit, resultWrapping(divisor), value / divisor[unit])
	override fun abs() = Angle(unit, wrapping, abs(value))

	/**
	 * always returns a [Wrapping.LINEAR] angle
	 *
	 * if the target is an [Angle] and is [Wrapping.WRAPPING], then the output will be in the domain [-PI, PI] | [-180, 180]
	 */
	override fun findError(target: Angle): Angle {
		return when (target.wrapping) {
			Wrapping.LINEAR -> target - this
			Wrapping.WRAPPING -> {
				val difference: Double = (target[unit] - this.value).mod(unit.wrapAt)
				if (difference > (unit.wrapAt / 2.0)) {
					return Angle(unit, Wrapping.LINEAR, -unit.wrapAt + difference)
				}
				return Angle(unit, Wrapping.LINEAR, difference)
			}
		}
	}
	override fun coerceAtLeast(minimumValue: Angle) = Angle(unit, wrapping, value.coerceAtLeast(minimumValue[unit]))
	override fun coerceAtMost(maximumValue: Angle) = Angle(unit, wrapping, value.coerceAtMost(maximumValue[unit]))
	override fun coerceIn(minimumValue: Angle, maximumValue: Angle) = Angle(unit, wrapping, value.coerceIn(minimumValue[unit], maximumValue[unit]))
	override fun pow(n: Double) = Angle(unit, wrapping, value.pow(n))
	override fun pow(n: Int) = Angle(unit, wrapping, value.pow(n))
	override fun sqrt() = Angle(unit, wrapping, sqrt(value))
	override fun toString() = "$value $unit"
	override fun equals(other: Any?) = other is Angle && abs((this - other).value) < 1e-12
	override fun hashCode(): Int = into(AngleUnits.RADIAN).value.hashCode()
	override fun compareTo(other: Angle) = intoRadians().value.compareTo(other[unit])

	companion object {
		@JvmField
		val NEGATIVE_INFINITY: Angle = Angle(AngleUnits.RADIAN, Wrapping.LINEAR, Double.NEGATIVE_INFINITY)
		@JvmField
		val POSITIVE_INFINITY: Angle = Angle(AngleUnits.RADIAN, Wrapping.LINEAR, Double.POSITIVE_INFINITY)
		@JvmField
		val NaN: Angle = Angle(AngleUnits.RADIAN, Wrapping.LINEAR, Double.NaN)
	}

	// quick intos
	fun intoDegrees() = into(AngleUnits.DEGREE)
	fun intoRadians() = into(AngleUnits.RADIAN)
	fun intoWrapping() = into(Wrapping.WRAPPING)
	fun intoLinear() = into(Wrapping.LINEAR)

	// trig
	val sin by lazy { sin(intoRadians().intoWrapping().value) }
	val cos by lazy { cos(intoRadians().intoWrapping().value) }
	val tan by lazy { tan(intoRadians().intoWrapping().value) }

	private fun resultWrapping(other: Angle) = if (this.wrapping == Wrapping.LINEAR || other.wrapping == Wrapping.LINEAR) Wrapping.LINEAR else Wrapping.WRAPPING
}

// quick intos
fun Supplier<out Angle>.into(unit: AngleUnit) = Supplier { get().into(unit) }
fun Supplier<out Angle>.intoRadians() = Supplier { get().intoRadians() }
fun Supplier<out Angle>.intoDegrees() = Supplier { get().intoDegrees() }
fun Supplier<out Angle>.into(wrapping: Wrapping) = Supplier { get().into(wrapping) }
fun Supplier<out Angle>.intoWrapping() = Supplier { get().intoWrapping() }
fun Supplier<out Angle>.intoLinear() = Supplier { get().intoLinear() }
