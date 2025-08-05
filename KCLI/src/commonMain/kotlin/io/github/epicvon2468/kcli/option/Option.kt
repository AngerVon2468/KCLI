package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI
import io.github.epicvon2468.kcli.exceptions.UninitialisedOptionException

import kotlin.reflect.KProperty

// TODO: Nullable support?
// TODO: Add separate interface to make actual users only able to access certain functions?

/**
 * Base interface for providing a type impl for a [KCLI] option argument.
 * @property thisRef The [KCLI] subclass of the [KProperty] that this option is a delegate for.
 * @property property The [KProperty] that this option is a delegate for.
 * @property default The default value of this option.
 * @property value The value of this option. Will be null until [init] is called.
 * @property shortNames Short names for the option that can be used as an arg.
 * @property longNames Long names for the option that can be used as an arg.
 * @author EpicVon2468 (Mavity The Madity)
 */
interface Option<T : Any?> {

	/**
	 * The default value of this option.
	 */
	var default: T?

	/**
	 * The value of this option. Will be null until [init] is called.
	 */
	var value: T?

	/**
	 * Short names for the option that can be used as an arg.
	 */
	val shortNames: MutableList<String>

	/**
	 * Long names for the option that can be used as an arg.
	 */
	val longNames: MutableList<String>

	fun setup(thisRef: KCLI, property: KProperty<*>) {
		thisRef.optionVars += property to this
		val name = property.name
		this.longNames += name
		this.shortNames += name[0].toString()
	}

	operator fun contains(name: String): Boolean = name in this.shortNames || name in this.longNames

	fun transform(input: String?): T

	fun init(input: String?) = this::value.set(this.transform(input))

	fun noInit(thisRef: KCLI, property: KProperty<*>): Nothing

	operator fun getValue(thisRef: KCLI, property: KProperty<*>): T =
		this.value ?: (this.default ?: this.noInit(thisRef, property))

	operator fun provideDelegate(
		thisRef: KCLI,
		property: KProperty<*>
	): Option<T> {
		println("Providing delegate for KCLI subclass '${thisRef::class.simpleName}', property '${property.name}'!")
		this.setup(thisRef, property)
		return this
	}

	/**
	 * Sets the default value of this option.
	 * @param default The value to set the default value to.
	 * @return This option, for chaining.
	 */
	fun default(default: T): Option<T> {
		this.default = default
		return this
	}

	/**
	 * Adds or replaces the short names of this option.
	 * @param names The value(s) to use.
	 * @param replace Whether the short names should be replaced or added to. Defaults to adding.
	 * @return This option, for chaining.
	 */
	fun shortName(vararg names: String, replace: Boolean = false): Option<T> {
		if (replace) this.shortNames.clear()
		this.shortNames += names
		return this
	}

	/**
	 * Adds or replaces the long names of this option.
	 * @param names The value(s) to use.
	 * @param replace Whether the long names should be replaced or added to. Defaults to adding.
	 * @return This option, for chaining.
	 */
	fun longName(vararg names: String, replace: Boolean = false): Option<T> {
		if (replace) this.longNames.clear()
		this.longNames += names
		return this
	}
}

abstract class OptionImpl<T : Any?> : Option<T> {

	override var default: T? = null

	override var value: T? = null

	override val shortNames: MutableList<String> = mutableListOf()

	override val longNames: MutableList<String> = mutableListOf()

	fun invalidInput(str: String?): Nothing = throw IllegalStateException("Invalid input: '$str'!")

	override fun noInit(thisRef: KCLI, property: KProperty<*>): Nothing = throw UninitialisedOptionException(
		"Option ${property.name} of KCLI ${thisRef::class.simpleName} wasn't initialised with args," +
			" and had no default value or a nullable default value."
	)
}