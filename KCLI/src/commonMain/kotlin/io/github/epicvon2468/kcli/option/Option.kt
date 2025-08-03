package io.github.epicvon2468.kcli.option

import io.github.epicvon2468.kcli.KCLI
import io.github.epicvon2468.kcli.exceptions.UninitialisedOptionException

import kotlin.reflect.KProperty

// TODO: Nullable support?

/**
 * Base class for providing a type impl for a [KCLI] option argument.
 * @param thisRef The [KCLI] subclass of the [KProperty] that this option is a delegate for.
 * @param property The [KProperty] that this option is a delegate for.
 * @property default The default value of this option.
 * @property value The value of this option. Will be null until [init] is called.
 * @property shortNames Short names for the option that can be used as an arg.
 * @property longNames Long names for the option that can be used as an arg.
 * @author EpicVon2468 (Mavity The Madity)
 */
abstract class Option<T : Any?>(thisRef: KCLI, property: KProperty<*>) {

	/**
	 * The default value of this option.
	 */
	open var default: T? = null

	/**
	 * The value of this option. Will be null until [init] is called.
	 */
	open var value: T? = null

	/**
	 * Short names for the option that can be used as an arg.
	 */
	protected open val shortNames: MutableList<String> = mutableListOf()

	/**
	 * Long names for the option that can be used as an arg.
	 */
	protected open val longNames: MutableList<String> = mutableListOf()

	init {
		thisRef.optionVars += property to this
		val name = property.name
		this.longNames += name
		this.shortNames += name[0].toString()
	}

	open operator fun contains(name: String): Boolean = name in this.shortNames || name in this.longNames

	fun invalidInput(str: String?): Nothing = throw IllegalStateException("Invalid input: '$str'!")

	abstract fun transform(input: String?): T

	open fun init(input: String?) = this::value.set(this.transform(input))

	private fun noInit(thisRef: KCLI, property: KProperty<*>): Nothing =
		throw UninitialisedOptionException(
			"Option ${property.name} of KCLI ${thisRef::class.simpleName} wasn't initialised with args," +
				" and had no default value or a nullable default value."
		)

	open operator fun getValue(thisRef: KCLI, property: KProperty<*>): T =
		this.value ?: (this.default ?: this.noInit(thisRef, property))

	// User functions

	open fun default(default: T): Option<T> {
		this.default = default
		return this
	}

	open fun shortName(vararg names: String, replace: Boolean = false): Option<T> {
		if (replace) this.shortNames.clear()
		this.shortNames += names
		return this
	}

	open fun longName(vararg names: String, replace: Boolean = false): Option<T> {
		if (replace) this.longNames.clear()
		this.longNames += names
		return this
	}
}