package com.aliucord.settings

import com.aliucord.api.SettingsAPI
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public class SettingsDelegate<T : Any>(
    private val defaultValue: T,
    private val settings: SettingsAPI
) : ReadWriteProperty<Any, T> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return settings.getUnknown(property.name, defaultValue) as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        settings.setUnknown(property.name, value)
    }
}

public fun <T : Any> SettingsAPI.delegate(
    defaultValue: T
): SettingsDelegate<T> = SettingsDelegate(defaultValue, this)
