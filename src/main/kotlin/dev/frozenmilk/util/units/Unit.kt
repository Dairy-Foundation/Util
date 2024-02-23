package dev.frozenmilk.util.units

interface Unit<U: Unit<U>> {
	/**
	 * value * toCommonRatio = value in common units
	 *
	 * @see toCommonUnit
	 */
	val toCommonRatio: Double
	fun into(unit: U, value: Double): Double = if (unit == this) value else unit.fromCommonUnit(toCommonUnit(value))
	fun toCommonUnit(value: Double): Double = value * toCommonRatio
	fun fromCommonUnit(value: Double): Double = value / toCommonRatio
}

abstract class ReifiedUnit<U: Unit<U>, RU: ReifiedUnit<U, RU>>(val unit: U, val value: Double) : Comparable<RU> {
	/**
	 * non-mutating
	 */
	abstract fun into(unit: U): RU
	/**
	 * non-mutating
	 */
	operator fun get(unit: U): Double = into(unit).value
	/**
	 * non-mutating
	 */
	abstract operator fun plus(reifiedUnit: RU): RU
	/**
	 * non-mutating
	 */
	abstract operator fun minus(reifiedUnit: RU): RU
	/**
	 * non-mutating
	 */
	abstract operator fun unaryPlus(): RU
	/**
	 * non-mutating
	 */
	abstract operator fun unaryMinus(): RU
	/**
	 * non-mutating
	 */
	abstract operator fun times(multiplier: Double): RU
	/**
	 * non-mutating
	 */
	abstract operator fun times(multiplier: RU): RU
	/**
	 * non-mutating
	 */
	abstract operator fun div(divisor: Double): RU
	/**
	 * non-mutating
	 */
	abstract operator fun div(divisor: RU): RU
	/**
	 * non-mutating
	 */
	abstract fun pow(n: Double): RU
	/**
	 * non-mutating
	 */
	abstract fun pow(n: Int): RU
	/**
	 * non-mutating
	 */
	abstract fun sqrt(): RU
	/**
	 * non-mutating
	 */
	abstract fun abs(): RU
	/**
	 * returns the error difference this and [target], for some units, this may be the same as target - this
	 */
	abstract fun findError(target: RU): RU
	/**
	 * non-mutating
	 */
	abstract fun coerceAtLeast(minimumValue: RU): RU
	/**
	 * non-mutating
	 */
	abstract fun coerceAtMost(maximumValue: RU): RU
	/**
	 * non-mutating
	 */
	abstract fun coerceIn(minimumValue: RU, maximumValue: RU): RU
	abstract override operator fun compareTo(other: RU): Int
	fun lessThan(other: RU) = compareTo(other) < 0
	fun lessThanEqualTo(other: RU) = compareTo(other) <= 0
	fun greaterThan(other: RU) = compareTo(other) > 0
	fun greaterThanEqualTo(other: RU) = compareTo(other) >= 0
	abstract override fun toString(): String
	abstract override fun equals(other: Any?): Boolean
	abstract override fun hashCode(): Int

	//
	// Number
	//
	fun isNaN(): Boolean = value.isNaN()
}