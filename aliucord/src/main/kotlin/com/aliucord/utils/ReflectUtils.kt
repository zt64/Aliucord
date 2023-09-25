/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.utils

import com.aliucord.Main
import java.lang.reflect.*

/** Utility class to ease Reflection  */
@Suppress("DiscouragedPrivateApi")
public object ReflectUtils {
    private var unsafe: Any? = null
    private var unsafeAllocIns: Method? = null
    private var accessFlagsFields: Field? = null

    /**
     * Creates new class instance without using a constructor
     * @param clazz Class
     * @return Created instance
     */
    @JvmStatic
    public fun <T> allocateInstance(clazz: Class<T>): T? {
        try {
            if (unsafeAllocIns == null) {
                val c = Class.forName("sun.misc.Unsafe")
                unsafe = getField(c, null, "theUnsafe")
                unsafeAllocIns = c.getMethod("allocateInstance", Class::class.java)
            }
            @Suppress("UNCHECKED_CAST")
            return unsafeAllocIns!!(unsafe, clazz) as T
        } catch (e: Throwable) {
            Main.logger.error(e)
        }
        return null
    }

    /** @see [allocateInstance] */
    @JvmStatic
    public inline fun <reified T> allocateInstance(): T? = allocateInstance(T::class.java)

    /**
     * Gets the constructor for class T matching the specified arguments
     *
     * @param clazz T.class
     * @param args  The arguments that should be passed to the constructor. arguments [ "hello", 12 ] would match constructor(String s, int i)
     * @param <T>   The class
     * @return The found constructor
     * @throws NoSuchMethodException No such constructor found
    </T> */
    @Throws(NoSuchMethodException::class)
    @JvmStatic
    public fun <T> getConstructorByArgs(clazz: Class<T>, vararg args: Any): Constructor<T> {
        val argTypes = arrayOfNulls<Class<*>>(args.size)
        for (i in args.indices) argTypes[i] = args[i].javaClass
        val c = clazz.getDeclaredConstructor(*argTypes)
        c.isAccessible = true
        return c
    }

    /**
     * Attempts to find and invoke the constructor of class T matching the specified arguments
     *
     * @param clazz T.class
     * @param args  The arguments to invoke the constructor with. arguments [ "hello", 12 ] would match constructor(String s, int i)
     * @param <T>   The class
     * @return The constructed Object
     * @throws NoSuchMethodException     No such constructor found
     * @throws IllegalAccessException    This constructor is inaccessible
     * @throws InvocationTargetException An exception occurred while invoking this constructor
     * @throws InstantiationException    This class cannot be constructed (is abstract, interface, etc)
    </T> */
    @Throws(
        NoSuchMethodException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    @JvmStatic
    public fun <T> invokeConstructorWithArgs(clazz: Class<T>, vararg args: Any): T {
        return getConstructorByArgs(clazz, *args).newInstance(*args)
    }

    /**
     * Attempts to find and invoke the method matching the specified arguments
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Method] to improve performance drastically
     *
     * @param clazz The class
     * @param methodName The name of the method
     * @param args  The arguments to invoke the method with. arguments [ "hello", 12 ] would match someMethod(String s, int i)
     * @return The found method
     * @throws NoSuchMethodException No such constructor found
     */
    @Throws(NoSuchMethodException::class)
    @JvmStatic
    public fun getMethodByArgs(clazz: Class<*>, methodName: String, vararg args: Any): Method {
        val argTypes = arrayOfNulls<Class<*>>(args.size)
        for (i in args.indices) argTypes[i] = args[i].javaClass
        val m = clazz.getDeclaredMethod(methodName, *argTypes)
        m.isAccessible = true
        return m
    }

    /**
     * Attempts to find and invoke the method matching the specified arguments
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Method] to improve performance drastically
     *
     * @param clazz The class holding the method
     * @param instance The instance of the class to invoke the method on or null to invoke static method
     * @param methodName The name of the method
     * @param args  The arguments to invoke the method with. arguments [ "hello", 12 ] would match someMethod(String s, int i)
     * @return The result of invoking the method
     * @throws NoSuchMethodException     No such method found
     * @throws IllegalAccessException    This method is inaccessible
     * @throws InvocationTargetException An exception occurred while invoking this method
     */
    @Throws(ReflectiveOperationException::class)
    @JvmStatic
    public fun invokeMethod(clazz: Class<*>, instance: Any?, methodName: String, vararg args: Any?): Any? {
        return getMethodByArgs(clazz, methodName, args).invoke(instance, *args)
    }

    /**
     * Attempts to find and invoke the method matching the specified arguments
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Method] to improve performance drastically
     *
     * @param instance The instance of the class to invoke the method on
     * @param methodName The name of the method
     * @param args  The arguments to invoke the method with. arguments [ "hello", 12 ] would match someMethod(String s, int i)
     * @return The result of invoking the method
     * @throws NoSuchMethodException     No such method found
     * @throws IllegalAccessException    This method is inaccessible
     * @throws InvocationTargetException An exception occurred while invoking this method
     */
    @Throws(ReflectiveOperationException::class)
    @JvmStatic
    public fun invokeMethod(instance: Any, methodName: String, vararg args: Any?): Any? {
        return invokeMethod(instance.javaClass, instance, methodName, *args)
    }

    /**
     * Gets a field declared in the class.
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Field] to improve performance drastically
     *
     * @param instance  Instance of the class where the field is located.
     * @param fieldName Name of the field.
     * @return Data stored in the field.
     * @throws NoSuchFieldException   If the field doesn't exist.
     * @throws IllegalAccessException If the field is inaccessible
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    @JvmStatic
    public fun getField(instance: Any, fieldName: String): Any? {
        return getField(instance.javaClass, instance, fieldName)
    }

    /**
     * Gets a field declared in the class.
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Field] to improve performance drastically
     *
     * @param clazz     [Class] where the field is located.
     * @param instance  Instance of the `clazz` or null to get static field
     * @param fieldName Name of the field.
     * @return Data stored in the field.
     * @throws NoSuchFieldException   If the field doesn't exist.
     * @throws IllegalAccessException If the field is inaccessible.
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    @JvmStatic
    public fun getField(clazz: Class<*>, instance: Any?, fieldName: String): Any? {
        return clazz.getDeclaredField(fieldName).apply {
            isAccessible = true
        }[instance]
    }

    /**
     * Override a field of a class.
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Field] to improve performance drastically
     *
     * @param instance  Instance of the class where the field is located.
     * @param fieldName Name of the field.
     * @param v         Value to store.
     * @throws NoSuchFieldException   If the field doesn't exist.
     * @throws IllegalAccessException If the field is inaccessible. Shouldn't happen.
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    @JvmStatic
    public fun setField(instance: Any, fieldName: String, v: Any?): Unit = setField(instance.javaClass, instance, fieldName, v)

    /**
     * Override a field of a class.
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Field] to improve performance drastically
     *
     * @param clazz     [Class] where the field is located.
     * @param instance  Instance of the `clazz` or null to set static field.
     * @param fieldName Name of the field.
     * @param v         Value to store.
     * @throws NoSuchFieldException   If the field doesn't exist.
     * @throws IllegalAccessException If the field is inaccessible. Shouldn't happen.
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    @JvmStatic
    public fun setField(clazz: Class<*>, instance: Any?, fieldName: String, v: Any?) {
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        field[instance] = v
    }

    /**
     * Override a final field of a class.
     * WARNING: If this field is of a primitive type, setting it may have no effect as the compiler will inline final primitives.
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Field] to improve performance drastically
     *
     * @param instance  Instance of the `clazz` or null to set static field.
     * @param fieldName Name of the field.
     * @param v         Value to store.
     * @throws NoSuchFieldException   If the field doesn't exist.
     * @throws IllegalAccessException If the field is inaccessible. Shouldn't happen.
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    @JvmStatic
    public fun setFinalField(instance: Any?, fieldName: String, v: Any?): Unit = setFinalField(instance!!.javaClass, instance, fieldName, v)

    /**
     * Override a final field of a class.
     * WARNING: If this field is of a primitive type, setting it may have no effect as the compiler will inline final primitives.
     * Please note that this does not cache the lookup result, so if you need to call this many times
     * you should do it manually and cache the [Field] to improve performance drastically
     *
     * @param clazz     [Class] where the field is located.
     * @param instance  Instance of the `clazz` or null to set static field.
     * @param fieldName Name of the field.
     * @param v         Value to store.
     * @throws NoSuchFieldException   If the field doesn't exist.
     * @throws IllegalAccessException If the field is inaccessible. Shouldn't happen.
     */
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    @JvmStatic
    public fun setFinalField(clazz: Class<*>, instance: Any?, fieldName: String, v: Any?) {
        if (accessFlagsFields == null) {
            accessFlagsFields = try {
                Field::class.java.getDeclaredField("accessFlags")
            } catch (ignored: ReflectiveOperationException) {
                try {
                    Field::class.java.getDeclaredField("modifiers")
                } catch (ex: ReflectiveOperationException) {
                    throw RuntimeException("Failed to retrieve accessFlags/modifiers field", ex)
                }
            }
            accessFlagsFields!!.isAccessible = true
        }
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        accessFlagsFields!![field] = field.modifiers and Modifier.FINAL.inv()
        field[instance] = v
    }
}
