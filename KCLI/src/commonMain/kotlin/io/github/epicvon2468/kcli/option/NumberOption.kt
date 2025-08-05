package io.github.epicvon2468.kcli.option

abstract class NumberOption<T : Number> : OptionImpl<T>() {

	abstract fun numberTransform(input: Number): T

	override fun transform(input: String?): T = this.numberTransform(input?.toDouble() ?: this.invalidInput(input))
}

open class DoubleOption : NumberOption<Double>() {

	override fun numberTransform(input: Number): Double = input.toDouble()
}

open class FloatOption : NumberOption<Float>() {

	override fun numberTransform(input: Number): Float = input.toFloat()
}

open class ShortOption : NumberOption<Short>() {

	override fun numberTransform(input: Number): Short = input.toShort()
}

open class IntOption : NumberOption<Int>() {

	override fun numberTransform(input: Number): Int = input.toInt()
}

open class LongOption : NumberOption<Long>() {

	override fun numberTransform(input: Number): Long = input.toLong()
}