@file:JvmName("Angles")
package dev.frozenmilk.util.units.angle

import dev.frozenmilk.util.units.ReifiedUnit
import dev.frozenmilk.util.units.Unit
import java.util.function.Supplier
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * common value is radians
 */
interface AngleUnit : Unit<AngleUnit> {
	val wrapAt: Double
	val relativeAt: Double
	override fun common() = AngleUnits.RADIAN
}

enum class AngleUnits(override val toCommonRatio: Double, override val wrapAt: Double) : AngleUnit {
	RADIAN(1.0, Math.PI * 2, ),
	DEGREE(Math.PI / 180.0, 360.0);

	override val relativeAt: Double = 0.5 * wrapAt
}

enum class Wrapping {
	/**
	 * the domain [0, 1.0) rotations | [0, 2 * PI) radians | [0, 360) degrees
	 */
	WRAPPING {
		override fun wrap(value: Double, unit: AngleUnit) = value.mod(unit.wrapAt)
	},
	/**
	 * the domain (-0.5, 0.5] rotations | (-PI, PI] radians | (-180, 180] degrees
	 */
	RELATIVE {
		override fun wrap(value: Double, unit: AngleUnit) = unit.relativeAt - ((unit.relativeAt - value).mod(unit.wrapAt))
	},
	/**
	 * unbounded domain
	 */
	LINEAR {
		override fun wrap(value: Double, unit: AngleUnit) = value
	};
	abstract fun wrap(value: Double, unit: AngleUnit): Double
}

class Angle
@JvmOverloads
constructor(
	unit: AngleUnit = AngleUnits.RADIAN,
	val wrapping: Wrapping = Wrapping.WRAPPING,
	value: Double = 0.0
) : ReifiedUnit<AngleUnit, Angle>(unit, wrapping.wrap(value, unit)) {
	override fun into(unit: AngleUnit) = if (unit == this.unit) this else Angle(unit, wrapping, this.unit.into(unit, value))
	fun into(unit: AngleUnit, wrapping: Wrapping) = if (unit == this.unit && wrapping == this.wrapping) this else Angle(unit, wrapping, this.unit.into(unit, value))
	fun into(wrapping: Wrapping) = if (wrapping == this.wrapping) this else Angle(unit, wrapping, value)
	/**
	 * if either this [wrapping] or [reifiedUnit]'s [wrapping] is [Wrapping.LINEAR], the result will be [Wrapping.LINEAR]
	 */
	override fun plus(reifiedUnit: Angle) = if (reifiedUnit.value == 0.0) this else Angle(unit, resultWrapping(reifiedUnit), value + reifiedUnit[unit])
	/**
	 * if either this [wrapping] or [reifiedUnit]'s [wrapping] is [Wrapping.LINEAR], the result will be [Wrapping.LINEAR]
	 */
	override fun minus(reifiedUnit: Angle) = if (reifiedUnit.value == 0.0) this else Angle(unit, resultWrapping(reifiedUnit), value - reifiedUnit[unit])
	override fun unaryPlus() = this
	override fun unaryMinus() = Angle(unit, wrapping, -value)
	override fun times(multiplier: Double) = if (multiplier == 1.0) this else Angle(unit, wrapping, value * multiplier)
	/**
	 * if either this [wrapping] or [multiplier]'s [wrapping] is [Wrapping.LINEAR], the result will be [Wrapping.LINEAR]
	 */
	override fun times(multiplier: Angle) = if (multiplier[unit] == 1.0) this else Angle(unit, resultWrapping(multiplier), value * multiplier[unit])
	override fun div(divisor: Double) = if (divisor == 1.0) this else Angle(unit, wrapping, value / divisor)
	/**
	 * if either this [wrapping] or [divisor]'s [wrapping] is [Wrapping.LINEAR], the result will be [Wrapping.LINEAR]
	 */
	override fun div(divisor: Angle) = if (divisor[unit] == 1.0) this else Angle(unit, resultWrapping(divisor), value / divisor[unit])
	override fun rem(divisor: Double) = if (divisor > value) this else Angle(unit, wrapping, value % divisor)
	override fun rem(divisor: Angle) = if (divisor[unit] > value) this else Angle(unit, wrapping, value % divisor[unit])
	override fun pow(n: Double) = if (n == 1.0) this else Angle(unit, wrapping, value.pow(n))
	override fun pow(n: Int) = if (n == 1) this else Angle(unit, wrapping, value.pow(n))
	override fun sqrt() = if (value == 1.0) this else Angle(unit, wrapping, sqrt(value))
	override val absoluteValue: Angle
		get() = if (sign == -1.0) Angle(unit, wrapping, value.absoluteValue) else this
	override val sign = value.sign

	/**
	 * returns error based on [target]'s [wrapping]
	 *
	 * [Wrapping.LINEAR] -> [Wrapping.LINEAR]
	 *
	 * [Wrapping.WRAPPING], [Wrapping.RELATIVE] -> [Wrapping.RELATIVE], these two will determine the shortest distance to the target angle
	 */
	override fun findError(target: Angle): Angle {
		return Angle(unit, if (target.wrapping == Wrapping.LINEAR) Wrapping.LINEAR else Wrapping.RELATIVE, target[unit] - value)
	}
	override fun coerceAtLeast(minimumValue: Angle) = if (minimumValue[unit] < value) this else minimumValue.into(unit)
	override fun coerceAtMost(maximumValue: Angle) = if (maximumValue[unit] > value) this else maximumValue.into(unit)
	override fun coerceIn(minimumValue: Angle, maximumValue: Angle) = if (minimumValue > maximumValue) throw IllegalArgumentException("$minimumValue should not be greater than $maximumValue") else if (!(minimumValue[unit] < value)) minimumValue.into(unit) else if (!(maximumValue[unit] > value)) maximumValue.into(unit) else this
	override fun toString() = "$value $unit"
	override fun equals(other: Any?) = other is Angle && (value - other[unit]).absoluteValue < 1e-12
	override fun hashCode(): Int = this[AngleUnits.RADIAN].hashCode()
	override fun compareTo(other: Angle) = value.compareTo(other[unit])

	companion object {
		@JvmField
		val NEGATIVE_INFINITY: Angle = Angle(AngleUnits.RADIAN, Wrapping.LINEAR, Double.NEGATIVE_INFINITY)
		@JvmField
		val POSITIVE_INFINITY: Angle = Angle(AngleUnits.RADIAN, Wrapping.LINEAR, Double.POSITIVE_INFINITY)
		@JvmField
		val NaN: Angle = Angle(AngleUnits.RADIAN, Wrapping.LINEAR, Double.NaN)

        fun inWrappingRange(angle: Angle) = inWrappingRange(angle.value, angle.unit)
		fun inWrappingRange(angle: Double, unit: AngleUnit) = angle in 0.0..<unit.wrapAt
		fun inRelativeRange(angle: Angle) = inRelativeRange(angle.value, angle.unit)
		fun inRelativeRange(angle: Double, unit: AngleUnit) = angle in (-unit.relativeAt)..<unit.relativeAt

		fun wrappingBehavior(angle: Double, unit: AngleUnit) = if (inWrappingRange(angle, unit)) Wrapping.WRAPPING else if (inRelativeRange(angle, unit)) Wrapping.RELATIVE else Wrapping.LINEAR
	}

	// quick intos
	fun intoDegrees() = into(AngleUnits.DEGREE)
	fun intoRadians() = into(AngleUnits.RADIAN)
	fun intoWrapping() = into(Wrapping.WRAPPING)
	fun intoRelative() = into(Wrapping.RELATIVE)
	fun intoLinear() = into(Wrapping.LINEAR)

	// trig
	val sin by lazy { sin(this[AngleUnits.RADIAN]) }
	val cos by lazy { cos(this[AngleUnits.RADIAN]) }
	val tan by lazy { tan(this[AngleUnits.RADIAN]) }

	private fun resultWrapping(other: Angle) = if (this.wrapping == Wrapping.LINEAR || other.wrapping == Wrapping.LINEAR) Wrapping.LINEAR else wrapping
}

// quick intos
fun Supplier<out Angle>.into(unit: AngleUnit) = Supplier { get().into(unit) }
fun Supplier<out Angle>.intoRadians() = Supplier { get().intoRadians() }
fun Supplier<out Angle>.intoDegrees() = Supplier { get().intoDegrees() }
fun Supplier<out Angle>.into(wrapping: Wrapping) = Supplier { get().into(wrapping) }
fun Supplier<out Angle>.intoWrapping() = Supplier { get().intoWrapping() }
fun Supplier<out Angle>.intoRelative() = Supplier { get().intoRelative() }
fun Supplier<out Angle>.intoLinear() = Supplier { get().intoLinear() }

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.DEGREE], wrapping behavior [Wrapping.WRAPPING]
 */
@get:JvmName("wrappedDeg")
val Int.wrappedDeg
	get() = this.toDouble().wrappedDeg

/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.DEGREE], wrapping behavior [Wrapping.WRAPPING]
 */
@get:JvmName("wrappedDeg")
val Double.wrappedDeg
	get() = Angle(AngleUnits.DEGREE, Wrapping.WRAPPING, this)

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.RADIAN], wrapping behavior [Wrapping.WRAPPING]
 */
@get:JvmName("wrappedRad")
val Int.wrappedRad
	get() = this.toDouble().wrappedRad

/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.RADIAN], wrapping behavior [Wrapping.WRAPPING]
 */
@get:JvmName("wrappedRad")
val Double.wrappedRad
	get() = Angle(AngleUnits.RADIAN, Wrapping.WRAPPING, this)

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.DEGREE], wrapping behavior [Wrapping.RELATIVE]
 */
@get:JvmName("relativeDeg")
val Int.relativeDeg
	get() = this.toDouble().relativeDeg

/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.DEGREE], wrapping behavior [Wrapping.RELATIVE]
 */
@get:JvmName("relativeDeg")
val Double.relativeDeg
	get() = Angle(AngleUnits.DEGREE, Wrapping.RELATIVE, this)

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.RADIAN], wrapping behavior [Wrapping.RELATIVE]
 */
@get:JvmName("relativeRad")
val Int.relativeRad
	get() = this.toDouble().relativeRad

/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.RADIAN], wrapping behavior [Wrapping.RELATIVE]
 */
@get:JvmName("relativeRad")
val Double.relativeRad
	get() = Angle(AngleUnits.RADIAN, Wrapping.RELATIVE, this)

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.DEGREE], wrapping behavior [Wrapping.LINEAR]
 */
@get:JvmName("linearDeg")
val Int.linearDeg
    get() = this.toDouble().linearDeg

/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.DEGREE], wrapping behavior [Wrapping.LINEAR]
 */
@get:JvmName("linearDeg")
val Double.linearDeg
    get() = Angle(AngleUnits.DEGREE, Wrapping.LINEAR, this)

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.RADIAN], wrapping behavior [Wrapping.LINEAR]
 */
@get:JvmName("linearRad")
val Int.linearRad
    get() = this.toDouble().linearRad

/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.RADIAN], wrapping behavior [Wrapping.LINEAR]
 */
@get:JvmName("linearRad")
val Double.linearRad
    get() = Angle(AngleUnits.RADIAN, Wrapping.LINEAR, this)


/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.DEGREE].
 * The [Wrapping] behavior will automatically determined through the following checks:
 * 	- if the angle is in the range for [Wrapping.WRAPPING], then [Wrapping.WRAPPING]
 * 	- if the angle is in the range for [Wrapping.RELATIVE], then [Wrapping.RELATIVE]
 *  - else, [Wrapping.LINEAR]
 */
@get:JvmName("deg")
val Double.deg
	get() = Angle(AngleUnits.DEGREE, Angle.wrappingBehavior(this, AngleUnits.DEGREE), this)

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.DEGREE].
 * The [Wrapping] behavior will automatically determined through the following checks:
 * 	- if the angle is in the range for [Wrapping.WRAPPING], then [Wrapping.WRAPPING]
 * 	- if the angle is in the range for [Wrapping.RELATIVE], then [Wrapping.RELATIVE]
 *  - else, [Wrapping.LINEAR]
 */
@get:JvmName("deg")
val Int.deg
	get() = this.toDouble().deg

/**
 * Conversion of an [Double] to an [Angle] with unit [AngleUnits.RADIAN].
 * The [Wrapping] behavior will automatically determined through the following checks:
 * 	- if the angle is in the range for [Wrapping.WRAPPING], then [Wrapping.WRAPPING]
 * 	- if the angle is in the range for [Wrapping.RELATIVE], then [Wrapping.RELATIVE]
 *  - else, [Wrapping.LINEAR]
 */
@get:JvmName("rad")
val Double.rad
	get() = Angle(AngleUnits.RADIAN, Angle.wrappingBehavior(this, AngleUnits.RADIAN), this)

/**
 * Conversion of an [Int] to an [Angle] with unit [AngleUnits.RADIAN].
 * The [Wrapping] behavior will automatically determined through the following checks:
 * 	- if the angle is in the range for [Wrapping.WRAPPING], then [Wrapping.WRAPPING]
 * 	- if the angle is in the range for [Wrapping.RELATIVE], then [Wrapping.RELATIVE]
 *  - else, [Wrapping.LINEAR]
 */
@get:JvmName("rad")
val Int.rad
	get() = this.toDouble().rad
