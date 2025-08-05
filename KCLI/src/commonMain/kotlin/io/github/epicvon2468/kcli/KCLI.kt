package io.github.epicvon2468.kcli

import io.github.epicvon2468.kcli.option.*
import io.github.epicvon2468.kcli.parser.getInfo

import kotlin.reflect.KProperty

open class KCLI {

	internal val optionVars: MutableMap<KProperty<*>, Option<*>> = mutableMapOf()

	inline fun <reified T> option(): Option<T> = OptionLookup.lookup<T>()

	open fun init(args: Array<String>) {
		// Why are for loop indexes immutable in Kotlin???
		val size = args.size
		var index = 0
		while (index < size) {
			val arg = args[index]
			val (optionInfo, newIndex) = getInfo(index, arg, args, index + 1 < size)
			index = newIndex // We want to make sure we're not going over values we already used.
			val option = this.optionVars.values.single { optionInfo.name in it }
			option.init(optionInfo.value)
			index++
		}
	}
}