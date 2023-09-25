/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.utils

public object MapUtils {
    /**
     * Finds the mapping key for Object val where Objects.equals(val, entry.value)
     * @param map The map to find the Object in
     * @param val The object to find the key of
     * @return Key of mapping or null if no such mapping exists
     */
    @JvmStatic
    public fun <K, V> getMapKey(map: Map<K, V>, `val`: V?): K? = map.firstNotNullOfOrNull { (key, value) ->
        if (`val` == value) key else null
    }
}
