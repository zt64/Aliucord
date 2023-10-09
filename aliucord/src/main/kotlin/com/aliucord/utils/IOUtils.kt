/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.utils

import java.io.*

public object IOUtils {
    /**
     * Reads the [InputStream] as text
     * @param is The input stream to read
     * @return The text
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun readAsText(`is`: InputStream): String {
        return `is`.reader().use(InputStreamReader::readText)
    }

    /**
     * Reads the InputStream into a `byte[]`
     * @param stream The stream to read
     * @return The read bytes
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun readBytes(stream: InputStream): ByteArray = stream.readBytes()

    /**
     * Pipe an [InputStream] into an [OutputStream]
     * @param is InputStream
     * @param os OutputStream
     * @throws IOException if an I/O error occurs
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun pipe(`is`: InputStream, os: OutputStream) {
        `is`.copyTo(os)
        os.flush()
    }
}
