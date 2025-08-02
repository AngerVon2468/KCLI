package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI
import io.github.epicvon2468.kcli.exceptions.UninitialisedOptionException
import io.github.epicvon2468.kcli.util.WriteOnlyModificationMap

import kotlin.reflect.*

// TODO: Nullable support?

abstract class Option<T : Any?> {

	private var _default: T? = null

	var value: T? = null

	val shortNames: MutableList<String> = mutableListOf()

	val longNames: MutableList<String> = mutableListOf()

	operator fun contains(name: String): Boolean = name in this.shortNames || name in this.longNames

	fun invalidInput(str: String?): Nothing = throw IllegalStateException("Invalid input: '$str'!")

	abstract fun transform(input: String?): T

	fun init(input: String?) = this::value.set(this.transform(input))

	fun default(default: T) = this::_default.set(default)

	private fun noInit(thisRef: KCLI, property: KProperty<*>): Nothing =
		throw UninitialisedOptionException(
			"Option ${property.name} of KCLI ${thisRef::class.simpleName} wasn't initialised with args," +
				" and had no default value or a nullable default value."
		)

	operator fun getValue(thisRef: KCLI, property: KProperty<*>): T =
		this.value ?: (this._default ?: this.noInit(thisRef, property))

	// TODO: This isn't getting called...
	operator fun provideDelegate(
		thisRef: KCLI,
		property: KProperty<*>
	): Option<T> {
		thisRef.optionVars += property to this
		val name = property.name
		this.longNames += name
		this.shortNames += name[0].toString()
		return this
	}
}

object OptionProvider {

	val lookup: MutableMap<KType, () -> Option<*>> = WriteOnlyModificationMap(mutableMapOf())

	init {
		this.lookup += typeOf<String>() to { StringOption() }
	}

	@Suppress("UNCHECKED_CAST")
	inline operator fun <reified T> provideDelegate(
		thisRef: KCLI,
		property: KProperty<*>
	): Option<T> = this.lookup[typeOf<T>()]?.invoke() as Option<T>?
		?: throw NotImplementedError("No option impl found for type ${T::class.simpleName}!")
}