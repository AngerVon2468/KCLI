package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI

import kotlin.reflect.KProperty

class StringOption(thisRef: KCLI, property: KProperty<*>) : Option<String>(thisRef, property) {

	// TODO: Move this to Parser logic?
	override fun transform(input: String?): String =
		input?.removePrefix("\"")?.removeSuffix("\"") ?: this.invalidInput(input)
}