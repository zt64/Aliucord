/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord

import android.util.Pair
import androidx.core.util.toAndroidPair

/** Utility class to work with collections, inspired by Javascript array methods  */
// Useless for Kotlin, kept for compatibility
@Suppress("unused")
public object CollectionUtils {
    /**
     * Check whether any element of the collection passes the filter
     * @return True if condition is true for any element in the collection
     */
    @JvmStatic
    public fun <E> some(collection: Collection<E?>, filter: (E?) -> Boolean): Boolean {
        return collection.any(filter)
    }

    /**
     * Check whether all elements of the collection pass the filter
     * @return True if condition is true for all elements in the collection
     */
    @JvmStatic
    public fun <E> every(collection: Collection<E>, filter: (E) -> Boolean): Boolean {
        return collection.all(filter)
    }

    /**
     * Find the first element which passes the filter
     * @return Element if found, otherwise null
     */
    @JvmStatic
    public fun <E> find(collection: Collection<E>, filter: (E) -> Boolean): E? {
        return collection.find(filter)
    }

    /**
     * Find the last element which passes the filter
     * @return Element if found, otherwise null
     */
    @JvmStatic
    public fun <E> findLast(collection: Collection<E>, filter: (E) -> Boolean): E? {
        return collection.last(filter)
    }

    /**
     * Find the index of the first element which passes the filter
     * @return Index if found, otherwise -1
     */
    @JvmStatic
    public fun <E> findIndex(list: List<E>, filter: (E) -> Boolean): Int {
        return list.indexOfFirst(filter)
    }

    /**
     * Find the index of the last element which passes the filter
     * @return Index if found, otherwise -1
     */
    @JvmStatic
    public fun <E> findLastIndex(list: List<E>, filter: Function1<E, Boolean>): Int {
        return list.indexOfLast(filter)
    }

    /**
     * Returns a new Array containing only the elements which passed the filter
     * @return Filtered Collection
     */
    @JvmStatic
    public fun <E> filter(collection: Collection<E>, filter: (E) -> Boolean): List<E> {
        return collection.filter(filter)
    }

    /**
     * Returns a new Array containing the results of the transform function for all elements
     * @return Filtered Collection
     */
    @JvmStatic
    public fun <E, R> map(collection: Collection<E>, transform: (E) -> R): List<R> {
        return collection.map(transform)
    }

    /**
     * Removes all elements from the collection which pass the filter
     * @return Whether an element was removed
     */
    @JvmStatic
    public fun <E> removeIf(
        collection: MutableCollection<E>,
        filter: (E) -> Boolean
    ): Boolean = collection.removeIf(filter)

    /**
     * Partition the collection into two Arrays. The first array has all elements which passed the filter, the second one has the rest
     * @return A [Pair] containing the two arrays
     */
    @JvmStatic
    public fun <E> partition(
        collection: Collection<E>,
        filter: (E) -> Boolean
    ): Pair<List<E>, List<E>> = collection.partition(filter).toAndroidPair()

    /**
     * Removes all elements after the specified start index
     * @return The removed elements
     */
    @JvmStatic
    public fun <E> splice(list: MutableList<E>, start: Int): List<E> {
        return splice(list, start, list.size - start)
    }

    /**
     * Removes the specified amount of elements after the specified start index and inserts the specified items
     * @param list The list of splice
     * @param start The start index
     * @param deleteCount The amount of items to remove
     * @param items The items to insert
     * @return The removed elements
     */
    @JvmStatic
    @SafeVarargs
    public fun <E> splice(
        list: MutableList<E>,
        start: Int,
        deleteCount: Int,
        vararg items: E
    ): List<E> {
        val ret = ArrayList<E>(deleteCount)
        for (i in 0 until deleteCount) ret += list.removeAt(start + i)
        list.addAll(start, items.toList())
        return ret
    }
}
