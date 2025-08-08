package io.github.epicvon2468.kcli

import kotlin.test.*

class TestKCLI : KCLI() {

	val abc: String by this.option()

	val def: Int by this.option()

	val ghi: Boolean by this.option()

	val jkl: String by this.option<String>()
		.shortName("b", "c") // Seems like type is lost after the first function call... I can live with this.

	val mno: Int by this.option<Int>()
		.shortName("l", replace = true)
}

class Tests {

	fun emulateProgramArgs(args: String): Array<String> = args.split(' ').toTypedArray()

	@Test
	fun cliTest() {
		val test = TestKCLI()

		test.optionVars.keys.joinToString(prefix = "[", postfix = "]") { it.name }.also(::println)

		test.init(emulateProgramArgs("--abc= \"test\" -d3"))
		assertEquals("test", test.abc)
		assertEquals(3, test.def)
		assertEquals(false, test.ghi)

		test.init(emulateProgramArgs("--ghi")) // This shouldn't be done outside of tests. I haven't made values reset
		assertEquals(true, test.ghi)

		test.init(emulateProgramArgs("-b\"hi\""))
		assertEquals("hi", test.jkl)

		test.init(emulateProgramArgs("-l -6 -c \"blah\""))
		assertEquals(-6, test.mno)
		assertEquals("blah", test.jkl)

		test.init(emulateProgramArgs("-l 6"))
		assertEquals(6, test.mno)

		test.init(emulateProgramArgs("-c \"hello\""))
		assertEquals("hello", test.jkl)
	}

	@Test
	fun cliTestStrings() {
		val test1 = TestKCLI()

		test1.init(emulateProgramArgs("--abc\"test\""))
		assertEquals("test", test1.abc)

		test1.init(emulateProgramArgs("-b\"multi word test\""))
		assertEquals("multi word test", test1.jkl)
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