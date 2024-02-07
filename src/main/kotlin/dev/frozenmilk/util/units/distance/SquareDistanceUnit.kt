package dev.frozenmilk.util.units.distance

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier

/**
 * common value is square millimeters
 */
interface SquareDistanceUnit : Unit<SquareDistanceUnit> {
	val oneDimensionUnit: DistanceUnit
}

enum class SquareDistanceUnits(override val toCommonRatio: Double, override val oneDimensionUnit: DistanceUnit) : SquareDistanceUnit {
	METER(1000.0 * 1000.0, DistanceUnits.METER),
	MILLIMETER(1.0, DistanceUnits.MILLIMETER),
	INCH(25.4 * 25.4, DistanceUnits.INCH),
	FOOT(304.8 * 304.8, DistanceUnits.FOOT),
}

class SquareDistance @JvmOverloads constructor(val distanceUnit: SquareDistanceUnit, value: Double = 0.0) : ReifiedUnit<SquareDistanceUnit, SquareDistance>(value) {
	override fun into(unit: SquareDistanceUnit) = SquareDistance(unit, distanceUnit.into(unit, value))
	override fun plus(reifiedUnit: ReifiedUnit<SquareDistanceUnit, SquareDistance>) = SquareDistance(distanceUnit, value + reifiedUnit.into(distanceUnit).value)
	override fun minus(reifiedUnit: ReifiedUnit<SquareDistanceUnit, SquareDistance>) = SquareDistance(distanceUnit, value - reifiedUnit.into(distanceUnit).value)
	override fun unaryPlus() = this
	override fun unaryMinus() = SquareDistance(distanceUnit, -value)
	override fun times(multiplier: Double) = SquareDistance(distanceUnit, value * multiplier)
	override fun div(divisor: Double) = SquareDistance(distanceUnit, value / divisor)
	fun div(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit.oneDimensionUnit, value / reifiedUnit.into(distanceUnit.oneDimensionUnit).value)

	override fun abs() = SquareDistance(distanceUnit, kotlin.math.abs(value))
	override fun coerceAtLeast(minimumValue: ReifiedUnit<SquareDistanceUnit, SquareDistance>) = SquareDistance(distanceUnit, value.coerceAtLeast(minimumValue.into(distanceUnit).value))
	override fun coerceAtMost(maximumValue: ReifiedUnit<SquareDistanceUnit, SquareDistance>) = SquareDistance(distanceUnit, value.coerceAtMost(maximumValue.into(distanceUnit).value))
	override fun coerceIn(minimumValue: ReifiedUnit<SquareDistanceUnit, SquareDistance>, maximumValue: ReifiedUnit<SquareDistanceUnit, SquareDistance>) = SquareDistance(distanceUnit, value.coerceIn(minimumValue.into(distanceUnit).value, maximumValue.into(distanceUnit).value))
	override fun compareTo(other: ReifiedUnit<SquareDistanceUnit, SquareDistance>): Int = value.compareTo(other.into(distanceUnit).value) // ignores power
	override fun toString() = "$value $distanceUnit"
	override fun equals(other: Any?): Boolean = other is SquareDistance && kotlin.math.abs((this - other).value) < 1e-12
	override fun hashCode(): Int = intoMillimeters().value.hashCode()

	companion object {
		@JvmStatic
		val NEGATIVE_INFINITY = SquareDistance(SquareDistanceUnits.MILLIMETER, Double.NEGATIVE_INFINITY)
		@JvmStatic
		val POSITIVE_INFINITY = SquareDistance(SquareDistanceUnits.MILLIMETER, Double.POSITIVE_INFINITY)
		@JvmStatic
		val NaN = SquareDistance(SquareDistanceUnits.MILLIMETER, Double.NaN)
	}

	// quick intos
	fun intoMillimeters() = into(SquareDistanceUnits.MILLIMETER)
	fun intoInches() = into(SquareDistanceUnits.INCH)
	fun intoFeet() = into(SquareDistanceUnits.FOOT)
	fun intoMeters() = into(SquareDistanceUnits.METER)
}

fun Supplier<out SquareDistance>.into(unit: SquareDistanceUnit) = Supplier<SquareDistance> { get().into(unit) }
fun Supplier<out SquareDistance>.intoMillimeters() = Supplier<SquareDistance> { get().intoMillimeters() }
fun Supplier<out SquareDistance>.intoInches() = Supplier<SquareDistance> { get().intoInches() }
fun Supplier<out SquareDistance>.intoFeet() = Supplier<SquareDistance> { get().intoFeet() }
fun Supplier<out SquareDistance>.intoMeters() = Supplier<SquareDistance> { get().intoMeters() }
