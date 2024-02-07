package dev.frozenmilk.util.units.distance

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.abs

/**
 * common value is millimeters
 */
interface DistanceUnit : Unit<DistanceUnit> {
	val twoDimensionUnit: SquareDistanceUnit
}
enum class DistanceUnits(override val toCommonRatio: Double, override val twoDimensionUnit: SquareDistanceUnit) : DistanceUnit {
	METER(1000.0, SquareDistanceUnits.METER),
	MILLIMETER(1.0, SquareDistanceUnits.MILLIMETER),
	INCH(25.4, SquareDistanceUnits.INCH),
	FOOT(304.8, SquareDistanceUnits.FOOT),
}

class Distance @JvmOverloads constructor(val distanceUnit: DistanceUnit, value: Double = 0.0) : ReifiedUnit<DistanceUnit, Distance>(value) {
	override fun into(unit: DistanceUnit) = Distance(unit, distanceUnit.into(unit, value))
	override fun plus(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, value + reifiedUnit.into(distanceUnit).value)
	override fun minus(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, value - reifiedUnit.into(distanceUnit).value)
	override fun unaryPlus() = this
	override fun unaryMinus() = Distance(distanceUnit, -value)
	override fun times(multiplier: Double) = Distance(distanceUnit, value * multiplier)
	operator fun times(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) = SquareDistance(distanceUnit.twoDimensionUnit, value * reifiedUnit.into(distanceUnit).value)
	override fun div(divisor: Double) = Distance(distanceUnit, value / divisor)
	fun findShortestDistance(reifiedUnit: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, reifiedUnit.into(distanceUnit).value - this.value)

	override fun abs() = Distance(distanceUnit, abs(value))
	override fun coerceAtLeast(minimumValue: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, value.coerceAtLeast(minimumValue.into(distanceUnit).value))
	override fun coerceAtMost(maximumValue: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, value.coerceAtMost(maximumValue.into(distanceUnit).value))
	override fun coerceIn(minimumValue: ReifiedUnit<DistanceUnit, Distance>, maximumValue: ReifiedUnit<DistanceUnit, Distance>) = Distance(distanceUnit, value.coerceIn(minimumValue.into(distanceUnit).value, maximumValue.into(distanceUnit).value))
	override fun compareTo(other: ReifiedUnit<DistanceUnit, Distance>): Int = value.compareTo(other.into(distanceUnit).value) // ignores power
	override fun toString() = "$value $distanceUnit"
	override fun equals(other: Any?): Boolean = other is Distance && abs((this - other).value) < 1e-12
	override fun hashCode(): Int = intoMillimeters().value.hashCode() // ignores power

	companion object {
		@JvmStatic
		val NEGATIVE_INFINITY: Distance = Distance(DistanceUnits.MILLIMETER, Double.NEGATIVE_INFINITY)
		@JvmStatic
		val POSITIVE_INFINITY: Distance = Distance(DistanceUnits.MILLIMETER, Double.POSITIVE_INFINITY)
		@JvmStatic
		val NaN: Distance = Distance(DistanceUnits.MILLIMETER, Double.NaN)
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
