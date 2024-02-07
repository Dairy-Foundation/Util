package dev.frozenmilk.util.units

import kotlin.math.pow

interface Unit<U: Unit<U>> {
	val toCommonRatio: Double
	fun into(unit: U, value: Double): Double = if (unit == this) value else unit.fromCommonUnit(toCommonUnit(value))
	fun toCommonUnit(value: Double): Double = value * toCommonRatio
	fun fromCommonUnit(value: Double): Double = value / toCommonRatio
}

abstract class ReifiedUnit<U: Unit<U>, RU: ReifiedUnit<U, RU>>(val value: Double) : Number(), Comparable<ReifiedUnit<U, RU>> {
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
	abstract operator fun plus(reifiedUnit: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract operator fun minus(reifiedUnit: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract operator fun unaryPlus(): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract operator fun unaryMinus(): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract operator fun times(multiplier: Double): ReifiedUnit<U, RU>
//	/**
//	 * non-mutating
//	 */
//	abstract operator fun times(reifiedUnit: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract operator fun div(divisor: Double): ReifiedUnit<U, RU>
//	/**
//	 * non-mutating
//	 */
//	abstract operator fun div(reifiedUnit: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
//	/**
//	 * non-mutating
//	 */
//	abstract fun findShortestDistance(reifiedUnit: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract fun abs(): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract fun coerceAtLeast(minimumValue: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract fun coerceAtMost(maximumValue: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
	/**
	 * non-mutating
	 */
	abstract fun coerceIn(minimumValue: ReifiedUnit<U, RU>, maximumValue: ReifiedUnit<U, RU>): ReifiedUnit<U, RU>
//	// todo review
////	/**
////	 * non-mutating
////	 */
////	abstract fun sqrt(): ReifiedUnit<U, RU>
//	/**
//	 * non-mutating
//	 */
//	abstract fun pow(n: Int): ReifiedUnit<U, RU>
	abstract override operator fun compareTo(other: ReifiedUnit<U, RU>): Int
	abstract override fun toString(): String
	abstract override fun equals(other: Any?): Boolean
	abstract override fun hashCode(): Int

	//
	// Number
	//
	fun isNaN(): Boolean = value.isNaN()
	override fun toByte() = value.toInt().toByte()
	override fun toDouble() = value
	override fun toFloat() = value.toFloat()
	override fun toInt() = value.toInt()
	override fun toLong() = value.toLong()
	override fun toShort() = value.toInt().toShort()
}