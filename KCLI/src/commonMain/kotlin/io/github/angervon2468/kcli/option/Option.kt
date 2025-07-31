package io.github.angervon2468.kcli.option

import io.github.angervon2468.kcli.KCLI
import io.github.angervon2468.kcli.exceptions.UninitialisedOptionException

import kotlin.reflect.KProperty

// TODO: Nullable support?

interface Option<T : Any?> {

	@Suppress("PropertyName")
	var _default: T?

	var value: T?

	fun transform(input: String): T

	fun default(default: T) = this::_default.set(default)

	private fun noInit(thisRef: KCLI, property: KProperty<*>): Nothing =
		throw UninitialisedOptionException(
			"Option ${property.name} of KCLI ${thisRef::class.simpleName} wasn't initialised with args," +
				"and had no default value or a nullable default value."
		)

	operator fun getValue(thisRef: KCLI, property: KProperty<*>): T =
		value ?: (_default ?: noInit(thisRef, property))

	operator fun provideDelegate(
		thisRef: KCLI,
		property: KProperty<*>
	): Option<T> {
		thisRef.optionVars += property
		return this
	}
}

abstract class OptionBase<T : Any?> : Option<T> {

	override var _default: T? = null

	override var value: T? = null
}