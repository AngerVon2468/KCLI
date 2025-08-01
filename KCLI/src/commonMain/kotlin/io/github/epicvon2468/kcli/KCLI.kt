package io.github.epicvon2468.kcli

import io.github.epicvon2468.kcli.option.*
import io.github.epicvon2468.kcli.parser.Parser

import kotlin.reflect.KProperty

open class KCLI {

	internal val optionVars: MutableMap<KProperty<*>, Option<*>> = mutableMapOf()

	fun option(): Option<String> = StringOption()

	fun init(args: Array<String>) {
		val parser = Parser()
		// Why are for loop indexes immutable in Kotlin???
		var index = 0
		while (index <= args.size) {
			val arg = args[index]
			val (optionInfo, newIndex) = parser.getInfo(index, arg, args)
			index = newIndex
			val option = this.optionVars.values.single { optionInfo.name in it }
		}
	}
}