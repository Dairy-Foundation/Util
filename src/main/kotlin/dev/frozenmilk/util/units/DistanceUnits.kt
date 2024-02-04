package dev.frozenmilk.util.units

import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.pow

/**
 * common value is millimeters
 */
interface DistanceUnit : Unit<DistanceUnit>
enum class DistanceUnits(override val toCommonRatio: Double) : DistanceUnit {
	METER(1000.0),
	MILLIMETER(1.0),
	INCH(25.4),
	FOOT(304.8),
}

class Distance(val distanceUnit: DistanceUnit = DistanceUnits.MILLIMETER, value: Double = 0.0, power: Int = 1) : ReifiedUnit<DistanceUnit, Distance>(value, power) {
	override fun into(unit: DistanceUnit) = Distance(unit, distanceUnit.into(unit, value, power), power)
	override fun plus(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) =
			if (power == reifiedUnit.power) Distance(distanceUnit, value + reifiedUnit.into(distanceUnit).value, power)
			else throw UnsupportedOperationException("Cannot add Distances with different powers.")
	override fun minus(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) =
			if (power == reifiedUnit.power) Distance(distanceUnit, value - reifiedUnit.into(distanceUnit).value, power)
			else throw UnsupportedOperationException("Cannot subtract Distances with different powers.")
	override fun unaryPlus() = this
	override fun unaryMinus() = Distance(distanceUnit, -value, power)
	override fun times(multiplier: Double) = Distance(distanceUnit, value * multiplier, power)
	override fun times(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, value * reifiedUnit.into(distanceUnit).value, power + reifiedUnit.power)
	override fun div(divisor: Double) = Distance(distanceUnit, value / divisor, power)
	override fun div(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, value / reifiedUnit.into(distanceUnit).value, power - reifiedUnit.power)
	override fun findShortestDistance(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>): ReifiedUnit<DistanceUnit, Distance> {
		if (power == reifiedUnit.power) return Distance(distanceUnit, reifiedUnit.into(distanceUnit).value - this.value, power)
		else throw UnsupportedOperationException("Cannot find shortest distance between Distances with different powers")
	}

	override fun abs() = Distance(distanceUnit, abs(value), power)
	override fun coerceAtLeast(minimumValue: ReifiedUnit<DistanceUnit, Distance>) =
			if (power == minimumValue.power) Distance(distanceUnit, value.coerceAtLeast(minimumValue.into(distanceUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce a Distance with another of a different power")
	override fun coerceAtMost(maximumValue: ReifiedUnit<DistanceUnit, Distance>) =
			if (power == maximumValue.power) Distance(distanceUnit, value.coerceAtMost(maximumValue.into(distanceUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce a Distance with another of a different power")
	override fun coerceIn(minimumValue: ReifiedUnit<DistanceUnit, Distance>, maximumValue: ReifiedUnit<DistanceUnit, Distance>) =
			if (power == minimumValue.power && power == maximumValue.power)
				Distance(distanceUnit, value.coerceIn(minimumValue.into(distanceUnit).value, maximumValue.into(distanceUnit).value), power)
			else throw UnsupportedOperationException("Cannot coerce a Distance with another of a different power")
	override fun pow(n: Int) = Distance(distanceUnit, value.pow(n), power * n)
	override fun compareTo(other: ReifiedUnit<DistanceUnit, Distance>): Int = value.compareTo(other.into(distanceUnit).value) // ignores power
	override fun toString() = "$value $distanceUnit $power"
	override fun equals(other: Any?): Boolean = other is Distance && abs((this - other).value) < 1e-12 && power == other.power
	override fun hashCode(): Int = intoMillimeters().value.hashCode() // ignores power

	companion object {
		val NEGATIVE_INFINITY: Distance = Distance(value = Double.NEGATIVE_INFINITY)
		val POSITIVE_INFINITY: Distance = Distance(value = Double.POSITIVE_INFINITY)
		val NaN: Distance = Distance(value = Double.NaN)
	}

	// quick intos
	fun intoMillimeters() = into(DistanceUnits.MILLIMETER)
	fun intoInches() = into(DistanceUnits.INCH)
	fun intoFeet() = into(DistanceUnits.FOOT)
	fun intoMeters() = into(DistanceUnits.METER)
}

fun Supplier<out Distance>.into(unit: DistanceUnit) = Supplier<Distance> { get().into(unit) }
fun Supplier<out Distance>.intoMillimeters() = Supplier<Distance> { get().intoMillimeters() }
fun Supplier<out Distance>.intoInches() = Supplier<Distance> { get().intoInches() }
fun Supplier<out Distance>.intoFeet() = Supplier<Distance> { get().intoFeet() }
fun Supplier<out Distance>.intoMeters() = Supplier<Distance> { get().intoMeters() }
