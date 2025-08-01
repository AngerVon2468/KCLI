package io.github.epicvon2468.kcli.parser

/**
 * For an option with name "example" and type Int, the following are valid inputs:
 *
 * `-e1`, `-e 1`, `-e=1`, `-e = 1`,
 * `--example1` `--example 1`, `--example=1`, `--example = 1`
 */
class Parser {

	fun getInfo(index: Int, arg: String, args: Array<String>): Pair<OptionInfo, Int> {
		val chars = arg.toCharArray()
		val prefix = chars.takeWhile { it == '-' }.joinToString(separator = "")
		val prefixType = PrefixType[prefix]
		prefixType.prefix
		TODO()
	}
}

// TODO: Support PowerShell `/` notation?
enum class PrefixType(val prefix: String) {

	MINUS_SHORT("-"),
	MINUS_LONG("--"),
	;

	companion object {

		val SHORT_PREFIX = PrefixType.entries.filter { it.prefix.length == 1 }
		val LONG_PREFIX = PrefixType.entries.filter { it.prefix.length > 1 }

		operator fun get(prefix: String): PrefixType = when (prefix.length) {
			0 -> throw IllegalStateException("Cannot provide PrefixType ")
			1 -> SHORT_PREFIX.first { it.prefix == prefix }
			else -> LONG_PREFIX.first { it.prefix == prefix }
		}
	}
}

/**
 * Information about a CLI option.
 * @property prefixType The kind of prefix used by this arg.
 * @property name The name of the arg. May be short or long name depending on [isShortName].
 * @property value The value of the provided option. This will be null if it is a flag (boolean option).
 */
data class OptionInfo(
	val prefixType: PrefixType,
	val name: String,
	val value: String?
) {

	/**
	 * @return if the [name] property should be read as a short or long name.
	 */
	val isShortName: Boolean
		get() = this.prefixType == PrefixType.MINUS_SHORT

	val isFlag: Boolean
		get() = this.value == null
}