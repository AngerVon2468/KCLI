package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI

import kotlin.reflect.KProperty

open class BooleanOption(thisRef: KCLI, property: KProperty<*>) : Option<Boolean>(thisRef, property) {

	init {
		if (this.default == null) this.default = false
	}

	override fun transform(input: String?): Boolean {
		if (input != null) throw IllegalStateException("Flag initialised with non-null value! This shouldn't happen!")
		return true // Because we were called in the first place.
	}
}

typealias FlagOption = BooleanOption