package io.github.angervon2468.kcli.exceptions

import kotlin.jvm.JvmOverloads

class UninitialisedOptionException @JvmOverloads constructor(
	message: String? = null,
	cause: Throwable? = null
) : Exception(message, cause)