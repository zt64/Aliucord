/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.utils

import java.lang.reflect.Field
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A lazy field delegate designed to improve the performance in field reflection.
 *
 * @param clazz     The class that the field belongs to.
 * @param fieldName The name of the field.
 */
public class LazyField<T> @PublishedApi internal constructor(private val clazz: Class<*>, private val fieldName: String?) : ReadOnlyProperty<T, Field> {
    private var v = null as Field?
    override fun getValue(thisRef: T, property: KProperty<*>): Field {
        return v ?: clazz.getDeclaredField(fieldName ?: property.name.removeSuffix("Field")).apply {
            isAccessible = true
            v = this
        }
    }
}

/**
 * A lazy field delegate designed to improve the performance in field reflection.
 *
 * @param fieldName The name of the field.
 */
public inline fun <reified T : Any> lazyField(fieldName: String? = null): LazyField<Any>  = LazyField(T::class.java, fieldName)
