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
		val size = args.size
		var index = 0
		while (index <= size) {
			val arg = args[index]
			val (optionInfo, newIndex) = parser.getInfo(index, arg, args, index < size)
			index = newIndex // We want to make sure we're not going over values we already used.
			val option = this.optionVars.values.single { optionInfo.name in it }
			index++
		}
	}
}