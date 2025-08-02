package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI

import kotlin.reflect.KProperty

class StringOption(thisRef: KCLI, property: KProperty<*>) : Option<String>(thisRef, property) {

	override fun transform(input: String?): String = input ?: this.invalidInput(input)
}