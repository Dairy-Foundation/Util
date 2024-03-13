package dev.frozenmilk.util.units.distance

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * common value is [DistanceUnits.MILLIMETER]
 */
interface DistanceUnit : Unit<DistanceUnit> {
	override fun common() = DistanceUnits.MILLIMETER
}
enum class DistanceUnits(override val toCommonRatio: Double) : DistanceUnit {
	METER(1000.0),
	MILLIMETER(1.0),
	INCH(25.4),
	FOOT(304.8),
}

class Distance (unit: DistanceUnit = DistanceUnits.MILLIMETER, value: Double = 0.0) : ReifiedUnit<DistanceUnit, Distance>(unit, value) {
	override fun into(unit: DistanceUnit) = if (unit == this.unit) this else Distance(unit, this.unit.into(unit, value))
	override fun plus(reifiedUnit: Distance) = Distance(unit, value + reifiedUnit[unit])
	override fun minus(reifiedUnit: Distance) = Distance(unit, value - reifiedUnit[unit])
	override fun unaryPlus() = this
	override fun unaryMinus() = Distance(unit, -value)
	override fun times(multiplier: Double) = Distance(unit, value * multiplier)
	override fun times(multiplier: Distance) = Distance(unit, value * multiplier[unit])
	override fun div(divisor: Double) = Distance(unit, value / divisor)
	override fun div(divisor: Distance) = Distance(unit, value / divisor[unit])
	override fun pow(n: Double) = Distance(unit, value.pow(n))
	override fun pow(n: Int) = Distance(unit, value.pow(n))
	override fun sqrt() = Distance(unit, sqrt(value))
	override fun abs() = Distance(unit, abs(value))
	override fun findError(target: Distance) = Distance(unit, target[unit] - value)
	override fun coerceAtLeast(minimumValue: Distance) = Distance(unit, value.coerceAtLeast(minimumValue[unit]))
	override fun coerceAtMost(maximumValue: Distance) = Distance(unit, value.coerceAtMost(maximumValue[unit]))
	override fun coerceIn(minimumValue: Distance, maximumValue: Distance) = Distance(unit, value.coerceIn(minimumValue[unit], maximumValue[unit]))
	override fun compareTo(other: Distance): Int = value.compareTo(other[unit])
	override fun toString() = "$value $unit"
	override fun equals(other: Any?): Boolean = other is Distance && abs((this - other).value) < 1e-12
	override fun hashCode(): Int = intoMillimeters().value.hashCode()

	companion object {
		@JvmField
		val NEGATIVE_INFINITY: Distance = Distance(DistanceUnits.MILLIMETER, Double.NEGATIVE_INFINITY)
		@JvmField
		val POSITIVE_INFINITY: Distance = Distance(DistanceUnits.MILLIMETER, Double.POSITIVE_INFINITY)
		@JvmField
		val NaN: Distance = Distance(DistanceUnits.MILLIMETER, Double.NaN)
	}

	// quick intos
	fun intoMillimeters() = into(DistanceUnits.MILLIMETER)
	fun intoInches() = into(DistanceUnits.INCH)
	fun intoFeet() = into(DistanceUnits.FOOT)
	fun intoMeters() = into(DistanceUnits.METER)
}

// quick intos
fun Supplier<out Distance>.into(unit: DistanceUnit) = Supplier { get().into(unit) }
fun Supplier<out Distance>.intoMillimeters() = Supplier { get().intoMillimeters() }
fun Supplier<out Distance>.intoInches() = Supplier { get().intoInches() }
fun Supplier<out Distance>.intoFeet() = Supplier { get().intoFeet() }
fun Supplier<out Distance>.intoMeters() = Supplier { get().intoMeters() }
