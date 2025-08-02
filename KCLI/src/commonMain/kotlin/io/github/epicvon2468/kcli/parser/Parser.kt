package io.github.epicvon2468.kcli.parser

/**
 * For an option with name "example" and type Int, the following are valid inputs:
 *
 * `-e1`, `-e 1`, `-e=1`, `-e = 1`,
 *
 * `--example1` `--example 1`, `--example=1`, `--example= 1`, `--example =1`, `--example = 1`
 */
fun getInfo(index: Int, arg: String, args: Array<String>, hasNext: Boolean): Pair<OptionInfo, Int> {
	val chars = arg.toCharArray()
	val nextIndex = index + 1
	val nextNextIndex = nextIndex + 1

	// Prefix info
	val prefix = chars.takeWhile { it == '-' }.joinToString(separator = "")
	val prefixType = PrefixType[prefix]

	fun isNextEquals(): Boolean = args[nextIndex] == "="
	fun isNextEqualsAndValue(): Boolean = args[nextIndex].startsWith('=')
	fun isNumber(str: String): Boolean = NUMBER_MATCHER matches str
	fun isQuotedValue(str: String): Boolean =
		(str.startsWith('"') && str.endsWith('"')) || (str.startsWith('\'') && str.endsWith('\''))
	fun exception(): Nothing = throw IllegalStateException("Could not parse args! Check your formatting and layout!")
	fun checkNextIsNotArg(check: String, supplier: () -> Pair<OptionInfo, Int>): Pair<OptionInfo, Int> =
		if (isQuotedValue(check) || isNumber(check)) supplier() else exception()

	// TODO: Cleanup indentation
	// Name & value
	val noPrefix = arg.substringAfter(prefix)
	if ('=' in noPrefix) {
		// If it ends on '=', we should try to read the next arg as a value
		// Otherwise, we can return the split name and value of the current arg
		return if (noPrefix.indexOf('=') != noPrefix.length) {
			if (!hasNext) exception()
			val next = args[nextIndex]
			return checkNextIsNotArg(next) {
				OptionInfo(prefixType, noPrefix.substringBeforeLast("="), next) to nextIndex
			}
		} else OptionInfo(prefixType, noPrefix.substringBefore('='), noPrefix.substringAfter('=')) to index
	} else if (hasNext) {
		val next = args[nextIndex]
		if (isNextEquals()) {
			val nextNext = args[nextNextIndex]
			return checkNextIsNotArg(nextNext) {
				OptionInfo(prefixType, noPrefix, nextNext) to nextNextIndex
			}
		} else if (isNextEqualsAndValue()) return OptionInfo(
			prefixType,
			noPrefix,
			next.substringAfter('=')
		) to nextIndex
	}
	TODO()
}

val NUMBER_MATCHER = Regex("(^-?\\d*\\.?\\d+)")

// TODO: Support PowerShell `/` notation?
enum class PrefixType(val prefix: String) {

	MINUS_SHORT("-"),
	MINUS_LONG("--"),
	;

	val isShortName: Boolean
		get() = this in SHORT_PREFIX

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
	val isShortName: Boolean = this.prefixType.isShortName

	val isFlag: Boolean = this.value == null
}