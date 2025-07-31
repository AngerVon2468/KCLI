package io.github.epicvon2468.kcli.option

class StringOption : OptionBase<String>() {

	override fun transform(input: String): String = input
}