package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.util.WriteOnlyModificationMap

import kotlin.reflect.*

object OptionLookup {

	val lookup: MutableMap<KType, (() -> Option<*>)> = WriteOnlyModificationMap(mutableMapOf())

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
	inline fun <reified T> lookup(): Option<T> = this.lookup[typeOf<T>()]?.invoke() as Option<T>?
		?: throw NotImplementedError("No option impl found for type ${T::class.simpleName}!")
}