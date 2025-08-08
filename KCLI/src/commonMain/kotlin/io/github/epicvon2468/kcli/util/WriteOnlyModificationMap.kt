package io.github.epicvon2468.kcli.util

class WriteOnlyModificationMap<K, V>(private val delegate: MutableMap<K, V>) : MutableMap<K, V> by delegate {

	fun disallowed(action: String): Nothing =
		throw UnsupportedOperationException("Write-only modification! $action is forbidden!")

	override fun remove(key: K): Nothing = this.disallowed("Remove")

	override fun put(key: K, value: V): V? =
		if (key in this) this.disallowed("Replace")
		else this.delegate.put(key, value)
}