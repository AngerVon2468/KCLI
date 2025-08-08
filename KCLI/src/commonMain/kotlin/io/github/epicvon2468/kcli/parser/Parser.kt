package io.github.epicvon2468.kcli.parser

// TODO: Support PowerShell `/` notation?
// TODO: FATAL STRING FLAW! WE DON'T KEEP SCANNING TO CHECK IF THE STRING IS NOT JUST ONE WORD!

internal var isTest: Boolean = false

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
	fun consumeUntilEndQuote(first: String, index: Int): Pair<String, Int> {
		val sb = StringBuilder(first)
		for (i in (index + 1)..<args.size) {
			val arg = args[i]
			if (isTest) sb.append(" ")
			sb.append(arg)
			if (arg.endsWith('"') || arg.endsWith('\'')) return sb.toString() to i
		}
		throw IllegalStateException("Opened quotation mark but didn't close!")
	}
	fun checkIsNotArg(
		check: String,
		index: Int,
		fail: (String, Int) -> Pair<OptionInfo, Int>? = { _, _ -> exception() },
		supplier: (String, Int) -> Pair<OptionInfo, Int>
	): Pair<OptionInfo, Int>? {
		val isQuoted = isQuotedValue(check)
		if (!isQuoted && (check.startsWith('"') || check.startsWith('\''))) {
			val (str, i) = consumeUntilEndQuote(check, index)
			return supplier(str, i)
		}
		return if (isQuoted || isNumber(check)) supplier(check, index) else fail(check, index)
	}
	fun valueInSameArg(input: String, index: Int): Pair<OptionInfo, Int> {
		val valueIndex: Int = input.substring(1).indexOfFirst {
			it in '0'..'9' || it == '"' || it == '\'' || it == '-'
		}.let { if (it == -1) it else it + 1 } // If the value was -1 (not found), and we added 1, we'd have 0...
		return if (valueIndex == -1) OptionInfo(prefix, input, null) to index // Flag
		// Else, we have a value in the same arg
		else {
			val name = input.take(valueIndex)
			val value = input.substring(valueIndex)
			if (!isQuotedValue(value) && (value.startsWith('"') || value.startsWith('\''))) {
				val (actualValue, index) = consumeUntilEndQuote(value, index)
				OptionInfo(prefix, name, actualValue) to index
			} else OptionInfo(prefix, name, value) to index
		}
	}

	if ('=' in noPrefix) {
		// If it ends on '=', we should try to read the next arg as a value
		// Otherwise, we can return the split name and value of the current arg
		return if (noPrefix.indexOf('=') == noPrefix.lastIndex) { // How was this working before??? What???
			if (!hasNext) exception()
			return checkIsNotArg(args[nIndex], nIndex) { value: String, index: Int ->
				OptionInfo(prefix, noPrefix.substringBeforeLast("="), value) to index
			}!!
		} else OptionInfo(prefix, noPrefix.substringBefore('='), noPrefix.substringAfter('=')) to index
	} else if (hasNext) {
		val next = args[nIndex]
		return if (isNextEqualsSign()) checkIsNotArg(args[nNIndex], nNIndex) { value: String, index: Int ->
			OptionInfo(prefix, noPrefix, value) to index
		}!!
		else if (isNextEqualsSignAndValue()) OptionInfo(prefix, noPrefix, next.substringAfter('=')) to nIndex
		else checkIsNotArg(
			next,
			nIndex,
			fail = { _, _ -> valueInSameArg(noPrefix, index) },
			// Whitespace separated args
			supplier = { value: String, index: Int -> OptionInfo(prefix, noPrefix, value) to index }
		)!!
	} else return valueInSameArg(noPrefix, index)
}

val NUMBER_MATCHER = Regex("(^-?\\d*\\.?\\d+)")

/**
 * Information about a CLI option.
 * @property prefix The prefix used by this arg.
 * @property name The name of the arg. May be short or long name depending on [isShortName].
 * @property value The value of the provided option. This will be null if it is a flag (boolean option).
 */
data class OptionInfo(
	val prefix: String,
	val name: String,
	val value: String?
) {

	/**
	 * @return if the [name] property should be read as a short or long name.
	 */
	val isShortName: Boolean = this.prefix.length == 1

	val isFlag: Boolean = this.value == null
}