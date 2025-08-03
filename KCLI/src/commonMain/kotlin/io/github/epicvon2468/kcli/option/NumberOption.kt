package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI

import kotlin.reflect.KProperty

abstract class NumberOption<T : Number>(thisRef: KCLI, property: KProperty<*>) : Option<T>(thisRef, property) {

	abstract fun numberTransform(input: Number): T

	override fun transform(input: String?): T = this.numberTransform(input?.toDouble() ?: this.invalidInput(input))
}

class DoubleOption(thisRef: KCLI, property: KProperty<*>) : NumberOption<Double>(thisRef, property) {

	override fun numberTransform(input: Number): Double = input.toDouble()
}

class FloatOption(thisRef: KCLI, property: KProperty<*>) : NumberOption<Float>(thisRef, property) {

	override fun numberTransform(input: Number): Float = input.toFloat()
}

class ShortOption(thisRef: KCLI, property: KProperty<*>) : NumberOption<Short>(thisRef, property) {

	override fun numberTransform(input: Number): Short = input.toShort()
}

class IntOption(thisRef: KCLI, property: KProperty<*>) : NumberOption<Int>(thisRef, property) {

	override fun numberTransform(input: Number): Int = input.toInt()
}

class LongOption(thisRef: KCLI, property: KProperty<*>) : NumberOption<Long>(thisRef, property) {

	override fun numberTransform(input: Number): Long = input.toLong()
}