package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI
import io.github.epicvon2468.kcli.exceptions.UninitialisedOptionException

import kotlin.reflect.KProperty

// TODO: Nullable support?

abstract class Option<T : Any?>(thisRef: KCLI, property: KProperty<*>) {

	var default: T? = null

	var value: T? = null

	protected val shortNames: MutableList<String> = mutableListOf()

	protected val longNames: MutableList<String> = mutableListOf()

	init {
		thisRef.optionVars += property to this
		val name = property.name
		this.longNames += name
		this.shortNames += name[0].toString()
	}

	operator fun contains(name: String): Boolean = name in this.shortNames || name in this.longNames

	fun invalidInput(str: String?): Nothing = throw IllegalStateException("Invalid input: '$str'!")

	abstract fun transform(input: String?): T

	fun init(input: String?) = this::value.set(this.transform(input))

	private fun noInit(thisRef: KCLI, property: KProperty<*>): Nothing =
		throw UninitialisedOptionException(
			"Option ${property.name} of KCLI ${thisRef::class.simpleName} wasn't initialised with args," +
				" and had no default value or a nullable default value."
		)

	operator fun getValue(thisRef: KCLI, property: KProperty<*>): T =
		this.value ?: (this.default ?: this.noInit(thisRef, property))

	// User functions

	fun default(default: T): Option<T> {
		this.default = default
		return this
	}

	fun shortName(vararg names: String, replace: Boolean = false): Option<T> {
		if (replace) this.shortNames.clear()
		this.shortNames += names
		return this
	}

	fun longName(vararg names: String, replace: Boolean = false): Option<T> {
		if (replace) this.longNames.clear()
		this.longNames += names
		return this
	}
}