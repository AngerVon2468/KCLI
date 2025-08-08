package io.github.epicvon2468.kcli.option

open class StringOption : OptionImpl<String>() {

	override fun transform(input: String?): String =
		input?.removePrefix("\"")?.removeSuffix("\"") ?: this.invalidInput(input)
}