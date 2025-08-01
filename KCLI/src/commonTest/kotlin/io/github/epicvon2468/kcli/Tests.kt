package io.github.epicvon2468.kcli

import kotlin.test.Test

class Tests {

	@Test
	fun cliTest() {
		val test = object : KCLI() {
			val abc: String by this.option()
		}
		test.optionVars.keys.joinToString(prefix = "[", postfix = "]") { it.name }.also(::println)
	}
}