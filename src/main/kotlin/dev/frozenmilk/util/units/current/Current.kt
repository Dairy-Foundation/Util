@file:JvmName("Currents")
package dev.frozenmilk.util.units.current

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * common unit is [CurrentUnits.AMP]
 */
interface CurrentUnit : Unit<CurrentUnit> {
	override fun common() = CurrentUnits.AMP
}

enum class CurrentUnits(override val toCommonRatio: Double) :
	CurrentUnit {
	AMP(1.0),
	MILLI_AMP(0.001);
}

class Current(unit: CurrentUnit, value: Double = 0.0) : ReifiedUnit<CurrentUnit, Current>(unit, value) {
	override fun into(unit: CurrentUnit) = if (unit == this.unit) this else Current(unit, this.unit.into(unit, value))
	override fun plus(reifiedUnit: Current) = if (reifiedUnit.value == 0.0) this else Current(unit, value + reifiedUnit[unit])
	override fun minus(reifiedUnit: Current) = if (reifiedUnit.value == 0.0) this else Current(unit, value - reifiedUnit[unit])
	override fun unaryPlus() = this
	override fun unaryMinus() = Current(unit, -value)
	override fun times(multiplier: Double) = if (multiplier == 1.0) this else Current(unit, value * multiplier)
	override fun times(multiplier: Current) = if (multiplier[unit] == 1.0) this else Current(unit, value * multiplier[unit])
	override fun div(divisor: Double) = if (divisor == 1.0) this else Current(unit, value / divisor)
	override fun div(divisor: Current) = if (divisor[unit] == 1.0) this else Current(unit, value / divisor[unit])
	override fun rem(divisor: Double) = if (divisor > value) this else Current(unit, value % divisor)
	override fun rem(divisor: Current) = if (divisor[unit] > value) this else Current(unit, value % divisor[unit])
	override fun pow(n: Double) = if (n == 1.0) this else Current(unit, value.pow(n))
	override fun pow(n: Int) = if (n == 1) this else Current(unit, value.pow(n))
	override fun sqrt() = if (value == 1.0) this else Current(unit, sqrt(value))
	override val absoluteValue: Current
		get() = if (sign == -1.0) Current(unit, value.absoluteValue) else this
	override val sign = value.sign
	override fun findError(target: Current) = Current(unit, target[unit] - value)
	override fun coerceAtLeast(minimumValue: Current) = if (minimumValue[unit] < value) this else minimumValue.into(unit)
	override fun coerceAtMost(maximumValue: Current) = if (maximumValue[unit] > value) this else maximumValue.into(unit)
	override fun coerceIn(minimumValue: Current, maximumValue: Current) = if (minimumValue > maximumValue) throw IllegalArgumentException("$minimumValue should not be greater than $maximumValue") else if (!(minimumValue[unit] < value)) minimumValue.into(unit) else if (!(maximumValue[unit] > value)) maximumValue.into(unit) else this
	override fun compareTo(other: Current): Int = value.compareTo(other[unit])
	override fun toString() = "$value $unit"
	override fun equals(other: Any?): Boolean = other is Current && (value - other[unit]).absoluteValue < 1e-12
	override fun hashCode(): Int = this[CurrentUnits.AMP].hashCode()

	companion object {
		@JvmField
		val NEGATIVE_INFINITY: Current = Current(CurrentUnits.AMP, Double.NEGATIVE_INFINITY)
		@JvmField
		val POSITIVE_INFINITY: Current = Current(CurrentUnits.AMP, Double.POSITIVE_INFINITY)
		@JvmField
		val NaN: Current = Current(CurrentUnits.AMP, Double.NaN)
	}

	// quick intos
	fun intoAmps() = into(CurrentUnits.AMP)
	fun intoMilliAmps() = into(CurrentUnits.MILLI_AMP)
}
// quick intos
fun Supplier<out Current>.into(unit: CurrentUnit) = Supplier { get().into(unit) }
fun Supplier<out Current>.intoAmps() = Supplier { get().intoAmps() }
fun Supplier<out Current>.intoMilliAmps() = Supplier { get().intoMilliAmps() }

val Double.amps
	get() = Current(CurrentUnits.AMP, this)

val Int.amps
	get() = this.toDouble().amps

val Double.milliAmps
	get() = Current(CurrentUnits.MILLI_AMP, this)

val Int.milliAmps
	get() = this.toDouble().milliAmps
