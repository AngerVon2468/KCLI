package io.github.epicvon2468.kcli

import kotlin.test.*

class Tests {

	fun emulateProgramArgs(fakeArgs: String): Array<String> = fakeArgs.split(' ').toTypedArray()

	@Test
	fun cliTest() {
		val test = object : KCLI() {
			val abc: String by this.option()

			val def: Int by this.option()

			val ghi: Boolean by this.option()

			val jkl: String by option()
		}
		test.init(emulateProgramArgs("--abc= \"test\" -d3"))
		test.optionVars.keys.joinToString(prefix = "[", postfix = "]") { it.name }.also(::println)
		assertEquals("test", test.abc)
		assertEquals(3, test.def)
		assertEquals(false, test.ghi)
		test.init(emulateProgramArgs("--ghi")) // This shouldn't be done outside of tests. I haven't made values reset
		assertEquals(true, test.ghi)
	}

	@Test
	fun numberRegexTest() {
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