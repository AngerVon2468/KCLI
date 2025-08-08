package io.github.epicvon2468.kcli.parser

// TODO: Support PowerShell `/` notation?
// TODO: FATAL STRING FLAW! WE DON'T KEEP SCANNING TO CHECK IF THE STRING IS NOT JUST ONE WORD!

/**
 * For an option with name "example" and type Int, the following are valid inputs:
 *
 * `-e1`, `-e 1`, `-e=1`, `-e = 1`,
 *
 * `--example1` `--example 1`, `--example=1`, `--example= 1`, `--example =1`, `--example = 1`
 * @return The latest [OptionInfo] parsed from the [args], and the last index that was used by the parser.
 */
fun getInfo(index: Int, arg: String, args: Array<String>, hasNext: Boolean): Pair<OptionInfo, Int> {
	val nIndex = index + 1 // nextIndex
	val nNIndex = nIndex + 1 // nextNextIndex

	// Prefix info
	val prefix = arg.takeWhile { it == '-' }
	// Name & value
	val noPrefix = arg.substringAfter(prefix)

	fun isNextEqualsSign(): Boolean = args[nIndex] == "="
	fun isNextEqualsSignAndValue(): Boolean = args[nIndex].startsWith('=') && !isNextEqualsSign()
	fun isNumber(str: String): Boolean = NUMBER_MATCHER matches str
	fun isQuotedValue(str: String): Boolean =
		(str.startsWith('"') && str.endsWith('"')) || (str.startsWith('\'') && str.endsWith('\''))
	fun exception(): Nothing = throw IllegalStateException("Could not parse args! Check your formatting and layout!")
	fun checkIsNotArg(
		check: String,
		fail: (String) -> Pair<OptionInfo, Int>? = { exception() },
		supplier: (String) -> Pair<OptionInfo, Int>
	): Pair<OptionInfo, Int>? {
		//val isQuoted = isQuotedValue(check)
		//if (!isQuoted && (check.startsWith('"') || check.startsWith('\''))) return null
		return if (isQuotedValue(check) || isNumber(check)) supplier(check) else fail(check)
	}
	fun valueInSameArg(): Pair<OptionInfo, Int> {
		val valueIndex: Int = noPrefix.substring(1).indexOfFirst {
			it in '0'..'9' || it == '"' || it == '\'' || it == '-'
		}.let { if (it == -1) it else it + 1 } // If the value was -1 (not found), and we added 1, we'd have 0...
		return if (valueIndex == -1) OptionInfo(prefix, noPrefix, null) to index // Flag
		// Else, we have a value in the same arg
		else OptionInfo(prefix, noPrefix.take(valueIndex), noPrefix.substring(valueIndex, noPrefix.length)) to index
	}

	if ('=' in noPrefix) {
		// If it ends on '=', we should try to read the next arg as a value
		// Otherwise, we can return the split name and value of the current arg
		return if (noPrefix.indexOf('=') == noPrefix.lastIndex) { // How was this working before??? What???
			if (!hasNext) exception()
			return checkIsNotArg(args[nIndex]) { OptionInfo(prefix, noPrefix.substringBeforeLast("="), it) to nIndex }!!
		} else OptionInfo(prefix, noPrefix.substringBefore('='), noPrefix.substringAfter('=')) to index
	} else if (hasNext) {
		val next = args[nIndex]
		return if (isNextEqualsSign()) checkIsNotArg(args[nNIndex]) { OptionInfo(prefix, noPrefix, it) to nNIndex }!!
		else if (isNextEqualsSignAndValue()) OptionInfo(prefix, noPrefix, next.substringAfter('=')) to nIndex
		else checkIsNotArg(
			next,
			fail = { valueInSameArg() },
			// Whitespace separated args
			supplier = { OptionInfo(prefix, noPrefix, it) to nIndex }
		)!!
	} else return valueInSameArg()
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