package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI
import io.github.epicvon2468.kcli.util.WriteOnlyModificationMap

import kotlin.reflect.*

object OptionProvider {

	val lookup: MutableMap<KType, ((KCLI, KProperty<*>) -> Option<*>)> = WriteOnlyModificationMap(mutableMapOf())

	init {
		this.lookup += typeOf<String>() to ::StringOption
		this.lookup += typeOf<Double>() to ::DoubleOption
		this.lookup += typeOf<Float>() to ::FloatOption
		this.lookup += typeOf<Short>() to ::ShortOption
		this.lookup += typeOf<Int>() to ::IntOption
		this.lookup += typeOf<Long>() to ::LongOption
		this.lookup += typeOf<Boolean>() to ::BooleanOption
	}

	@Suppress("UNCHECKED_CAST")
	inline operator fun <reified T> provideDelegate(
		thisRef: KCLI,
		property: KProperty<*>
	): Option<T> = this.lookup[typeOf<T>()]?.invoke(thisRef, property) as Option<T>?
		?: throw NotImplementedError("No option impl found for type ${T::class.simpleName}!")
}