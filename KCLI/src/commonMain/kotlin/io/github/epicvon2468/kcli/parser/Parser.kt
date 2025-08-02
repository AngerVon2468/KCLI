package io.github.epicvon2468.kcli.parser

// TODO: Support PowerShell `/` notation?
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

	fun isNextEquals(): Boolean = args[nextIndex] == "="
	fun isNextEqualsAndValue(): Boolean = args[nextIndex].startsWith('=')
	fun isNumber(str: String): Boolean = NUMBER_MATCHER matches str
	fun isQuotedValue(str: String): Boolean =
		(str.startsWith('"') && str.endsWith('"')) || (str.startsWith('\'') && str.endsWith('\''))
	fun exception(): Nothing = throw IllegalStateException("Could not parse args! Check your formatting and layout!")
	fun checkNextIsNotArg(
		check: String,
		fail: () -> Pair<OptionInfo, Int>? = { exception() },
		supplier: () -> Pair<OptionInfo, Int>
	): Pair<OptionInfo, Int>? = if (isQuotedValue(check) || isNumber(check)) supplier() else fail()

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
				OptionInfo(prefix, noPrefix.substringBeforeLast("="), next) to nextIndex
			}!!
		} else OptionInfo(prefix, noPrefix.substringBefore('='), noPrefix.substringAfter('=')) to index
	} else if (hasNext) {
		val next = args[nextIndex]
		if (isNextEquals()) {
			val nextNext = args[nextNextIndex]
			return checkNextIsNotArg(nextNext) { OptionInfo(prefix, noPrefix, nextNext) to nextNextIndex }!!
		} else if (isNextEqualsAndValue()) return OptionInfo(
			prefix,
			noPrefix,
			next.substringAfter('=')
		) to nextIndex
		else return checkNextIsNotArg(
			next,
			// Flag
			fail = { OptionInfo(prefix, noPrefix, null) to index },
			// Whitespace separated args
			supplier = { OptionInfo(prefix, noPrefix, next) to nextIndex }
		)!!
	} else return OptionInfo(prefix, noPrefix, null) to index // Flag
}

val NUMBER_MATCHER = Regex("(^-?\\d*\\.?\\d+)")

/**
 * Information about a CLI option.
 * @property prefixType The kind of prefix used by this arg.
 * @property name The name of the arg. May be short or long name depending on [isShortName].
 * @property value The value of the provided option. This will be null if it is a flag (boolean option).
 */
data class OptionInfo(
	val prefixType: String,
	val name: String,
	val value: String?
) {

	/**
	 * @return if the [name] property should be read as a short or long name.
	 */
	val isShortName: Boolean = this.prefixType.length == 1

	val isFlag: Boolean = this.value == null
}