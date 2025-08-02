package io.github.epicvon2468.kcli.util

class WriteOnlyModificationMap<K, V>(private val delegate: MutableMap<K, V>) : MutableMap<K, V> by delegate {

	override fun remove(key: K): Nothing =
		throw UnsupportedOperationException("Write-only modification! Remove is forbidden!")

	override fun put(key: K, value: V): V? = if (key in this)
		throw UnsupportedOperationException("Write-only modification! Replace is forbidden!")
	else this.delegate.put(key, value)
}