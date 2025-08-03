package io.github.epicvon2468.kcli

import kotlin.test.*

class Tests {

	@Test
	fun cliTest() {
		val test = object : KCLI() {
			val abc: String by this.option()

			val def: Int by this.option()
		}
		test.init(arrayOf("--abc=", "\"test\"", "-d3"))
		test.optionVars.keys.joinToString(prefix = "[", postfix = "]") { it.name }.also(::println)
		assertEquals("test", test.abc)
		assertEquals(3, test.def)
	}

	@Test
	fun regexTest() {
		val regex = Regex("(^-?\\d*\\.?\\d+)")
		assertTrue { regex matches "9" }
		assertTrue { regex matches "-2" }
		assertTrue { regex matches "7864243" }
		assertTrue { regex matches "-7864243" }
		assertTrue { regex matches "14534.323" }
		assertTrue { regex matches "-14534.323" }
		assertTrue { regex matches "3.32432" }
		assertTrue { regex matches "-3.32432" }
		assertTrue { regex matches "894.6" }
		assertTrue { regex matches "-894.6" }
		assertTrue { regex matches "5.4" }
		assertTrue { regex matches "-5.4" }
	}
}