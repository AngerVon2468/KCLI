package io.github.angervon2468.kcli.option

class StringOption : OptionBase<String>() {

	override fun transform(input: String): String = input
}