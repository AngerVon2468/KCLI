package io.github.epicvon2468.kcli.option

class StringOption : Option<String>() {

	override fun transform(input: String): String = input
}