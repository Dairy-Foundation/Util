@file:JvmName("Distances")
package dev.frozenmilk.util.units.distance

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * common value is [DistanceUnits.MILLIMETER]
 */
interface DistanceUnit : Unit<DistanceUnit> {
	override fun common() = DistanceUnits.MILLIMETER
}
enum class DistanceUnits(override val toCommonRatio: Double) : DistanceUnit {
	METER(1000.0),
	CENTIMETER(100.0),
	MILLIMETER(1.0),
	INCH(25.4),
	FOOT(304.8),
}

class Distance (unit: DistanceUnit = DistanceUnits.MILLIMETER, value: Double = 0.0) : ReifiedUnit<DistanceUnit, Distance>(unit, value) {
	override fun into(unit: DistanceUnit) = if (unit == this.unit) this else Distance(unit, this.unit.into(unit, value))
	override fun plus(reifiedUnit: Distance) = if (reifiedUnit.value == 0.0) this else Distance(unit, value + reifiedUnit[unit])
	override fun minus(reifiedUnit: Distance) = if (reifiedUnit.value == 0.0) this else Distance(unit, value - reifiedUnit[unit])
	override fun unaryPlus() = this
	override fun unaryMinus() = Distance(unit, -value)
	override fun times(multiplier: Double) = if (multiplier == 1.0) this else Distance(unit, value * multiplier)
	override fun times(multiplier: Distance) = if (multiplier[unit] == 1.0) this else Distance(unit, value * multiplier[unit])
	override fun div(divisor: Double) = if (divisor == 1.0) this else Distance(unit, value / divisor)
	override fun div(divisor: Distance) = if (divisor[unit] == 1.0) this else Distance(unit, value / divisor[unit])
	override fun rem(divisor: Double) = if (divisor > value) this else Distance(unit, value % divisor)
	override fun rem(divisor: Distance) = if (divisor[unit] > value) this else Distance(unit, value % divisor[unit])
	override fun pow(n: Double) = if (n == 1.0) this else Distance(unit, value.pow(n))
	override fun pow(n: Int) = if (n == 1) this else Distance(unit, value.pow(n))
	override fun sqrt() = if (value == 1.0) this else Distance(unit, sqrt(value))
	override val absoluteValue: Distance
		get() = if (sign == -1.0) Distance(unit, value.absoluteValue) else this
	override val sign = value.sign
	override fun findError(target: Distance) = Distance(unit, target[unit] - value)
	override fun coerceAtLeast(minimumValue: Distance) = if (minimumValue[unit] < value) this else minimumValue.into(unit)
	override fun coerceAtMost(maximumValue: Distance) = if (maximumValue[unit] > value) this else maximumValue.into(unit)
	override fun coerceIn(minimumValue: Distance, maximumValue: Distance) = if (minimumValue > maximumValue) throw IllegalArgumentException("$minimumValue should not be greater than $maximumValue") else if (!(minimumValue[unit] < value)) minimumValue.into(unit) else if (!(maximumValue[unit] > value)) maximumValue.into(unit) else this
	override fun compareTo(other: Distance): Int = value.compareTo(other[unit])
	override fun toString() = "$value $unit"
	override fun equals(other: Any?): Boolean = other is Distance && (this - other).value.absoluteValue < 1e-12
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
	fun intoCentimeters() = into(DistanceUnits.CENTIMETER)
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

// kotlin sugar
@get:JvmName("inch")
val Double.inch
	get() = Distance(DistanceUnits.INCH, this)

@get:JvmName("inch")
val Int.inch
	get() = this.toDouble().inch

@get:JvmName("ft")
val Double.ft
	get() = Distance(DistanceUnits.FOOT, this)

@get:JvmName("ft")
val Int.ft
	get() = this.toDouble().ft

@get:JvmName("m")
val Double.m
	get() = Distance(DistanceUnits.METER, this)

@get:JvmName("m")
val Int.m
	get() = this.toDouble().m

@get:JvmName("mm")
val Double.mm
	get() = Distance(DistanceUnits.MILLIMETER, this)

@get:JvmName("mm")
val Int.mm
	get() = this.toDouble().mm

@get:JvmName("cm")
val Double.cm
	get() = Distance(DistanceUnits.CENTIMETER, this)

@get:JvmName("cm")
val Int.cm
	get() = this.toDouble().cm