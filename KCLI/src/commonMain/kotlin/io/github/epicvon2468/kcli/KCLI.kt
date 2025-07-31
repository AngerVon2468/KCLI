package io.github.epicvon2468.kcli

import io.github.epicvon2468.kcli.option.StringOption

import kotlin.reflect.KProperty

open class KCLI {

	val test by this.option()

	internal val optionVars: MutableList<KProperty<*>> = mutableListOf()

	fun option(): StringOption = StringOption()
}