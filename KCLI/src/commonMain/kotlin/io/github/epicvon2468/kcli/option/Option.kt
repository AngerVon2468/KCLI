package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI
import io.github.epicvon2468.kcli.exceptions.UninitialisedOptionException

import kotlin.reflect.KProperty

// TODO: Nullable support?

abstract class Option<T : Any?> {

	private var _default: T? = null

	var value: T? = null

	val shortNames: MutableList<String> = mutableListOf()

	val longNames: MutableList<String> = mutableListOf()

	operator fun contains(name: String): Boolean = name in this.shortNames || name in this.longNames

	abstract fun transform(input: String): T

	fun init(input: String) = this::value.set(this.transform(input))

	fun default(default: T) = this::_default.set(default)

	private fun noInit(thisRef: KCLI, property: KProperty<*>): Nothing =
		throw UninitialisedOptionException(
			"Option ${property.name} of KCLI ${thisRef::class.simpleName} wasn't initialised with args," +
				" and had no default value or a nullable default value."
		)

	operator fun getValue(thisRef: KCLI, property: KProperty<*>): T =
		this.value ?: (this._default ?: this.noInit(thisRef, property))

	operator fun provideDelegate(
		thisRef: KCLI,
		property: KProperty<*>
	): Option<T> {
		thisRef.optionVars += property to this
		return this
	}
}