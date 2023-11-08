/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2023 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord

import com.aliucord.utils.GsonUtils.fromJson
import com.aliucord.utils.GsonUtils.gson
import com.aliucord.utils.GsonUtils.toJson
import com.aliucord.utils.IOUtils.pipe
import com.aliucord.utils.IOUtils.readAsText
import com.aliucord.utils.IOUtils.readBytes
import com.aliucord.utils.RNSuperProperties
import com.aliucord.utils.RNSuperProperties.superPropertiesBase64
import com.discord.utilities.analytics.AnalyticSuperProperties
import com.discord.utilities.rest.RestAPI.AppHeadersProvider
import com.google.gson.Gson
import java.io.*
import java.lang.reflect.Type
import java.math.BigInteger
import java.net.*
import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*

/** Http Utilities  */
@Suppress("unused")
public object Http {
    /**
     * Send a simple GET request
     *
     * @param url The url to fetch
     * @return Raw response (String). If you want Json, use simpleJsonGet
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun simpleGet(url: String?): String = Request(url).execute().text()

    /**
     * Download content from the specified url to the specified [File]
     *
     * @param url        The url to download content from
     * @param outputFile The file to save to
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun simpleDownload(url: String?, outputFile: File) {
        Request(url).execute().saveToFile(outputFile)
    }

    /**
     * Send a simple GET request
     *
     * @param url    The url to fetch
     * @param schema Class to [deserialize](https://en.wikipedia.org/wiki/Serialization) the response into
     * @return Response Object
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun <T> simpleJsonGet(url: String?, schema: Type?): T? {
        return gson.fromJson(simpleGet(url), schema)
    }

    /**
     * Send a simple GET request
     *
     * @param url    The url to fetch
     * @param schema Class to [deserialize](https://en.wikipedia.org/wiki/Serialization) the response into
     * @return Response Object
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun <T> simpleJsonGet(url: String?, schema: Class<T>?): T? {
        return simpleJsonGet(url, schema as Type?)
    }

    /**
     * Send a simple POST request
     *
     * @param url  The url to fetch
     * @param body The request body
     * @return Raw response (String). If you want Json, use simpleJsonPost
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun simplePost(url: String?, body: String): String {
        return Request(url, "POST").executeWithBody(body).text()
    }

    /**
     * Send a simple POST request and parse the JSON response
     *
     * @param url    The url to fetch
     * @param body   The request body
     * @param schema Class to [deserialize](https://en.wikipedia.org/wiki/Serialization) the response into
     * @return Response deserialized into the provided Class
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun <T> simpleJsonPost(url: String?, body: String, schema: Type?): T {
        return gson.fromJson(simplePost(url, body), schema)
    }

    // This is just here for proper Generics so you can do simpleJsonPost(url, body, myClass).myMethod() without having to cast
    /**
     * Send a simple POST request and parse the JSON response
     *
     * @param url    The url to fetch
     * @param body   The request body
     * @param schema Class to [deserialize](https://en.wikipedia.org/wiki/Serialization) the response into
     * @return Response deserialized into the provided Class
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun <T> simpleJsonPost(url: String?, body: String, schema: Class<T>?): T {
        return simpleJsonPost(url, body, schema as Type?)
    }

    /**
     * Send a simple POST request with JSON body
     *
     * @param url    The url to fetch
     * @param body   The request body
     * @param schema Class to [deserialize](https://en.wikipedia.org/wiki/Serialization) the response into
     * @return Response deserialized into the provided Class
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun <T> simpleJsonPost(url: String?, body: Any?, schema: Type?): T {
        return Request(url).executeWithJson(body).json(schema)
    }
    // This is just here for proper Generics so you can do simpleJsonPost(url, body, myClass).myMethod() without having to cast
    /**
     * Send a simple POST request with JSON body
     *
     * @param url    The url to fetch
     * @param body   The request body
     * @param schema Class to [deserialize](https://en.wikipedia.org/wiki/Serialization) the response into
     * @return Response deserialized into the provided Class
     */
    @Throws(IOException::class)
    @JvmStatic
    public fun <T> simpleJsonPost(url: String?, body: Any?, schema: Class<T>?): T {
        return simpleJsonPost(url, body, schema as Type?)
    }

    /**
     * @property req The request object
     * @property res The response object
     */
    public data class HttpException(val req: Request, val res: Response) : IOException() {
        /** The url of this request  */
        val url: URL = req.conn.url

        /** The HTTP method of this request  */
        val method: String = req.conn.requestMethod

        /** The status code of the response  */
        val statusCode: Int = res.statusCode

        /** The status message of the response  */
        val statusMessage: String = res.statusMessage
        override var message: String? = null
            get() {
                if (field == null) {
                    field = buildString {
                        append("${res.statusCode}: ${res.statusMessage} (${req.conn.url})")
                        runCatching {
                            req.conn.errorStream.use { eis ->
                                appendLine()
                                append(readAsText(eis))
                            }
                        }
                    }
                }
                return field
            }
            private set

    }

    /** QueryString Builder  */
    public class QueryBuilder(baseUrl: String) {
        private val sb = StringBuilder("$baseUrl?")

        /**
         * Append query parameter. Will automatically be encoded for you
         *
         * @param key   The parameter key
         * @param value The parameter value
         * @return self
         */
        public fun append(key: String?, value: String?): QueryBuilder = apply {
            val encKey = URLEncoder.encode(key, "UTF-8")
            val encValue = URLEncoder.encode(value, "UTF-8")
            sb.append(encKey).append('=').append(encValue).append('&')
        }

        /**
         * Build the finished Url
         */
        override fun toString(): String {
            val str = sb.toString()
            return str.substring(0, str.length - 1) // Remove last & or ? if no query specified
        }
    }


    /**
     * Construct a new MultiPartBuilder writing to the provided OutputStream
     *
     * @param boundary Boundary
     * @param outputStream [OutputStream] to write to. Should optimally be the result of connection.getOutputStream()
     */
    public class MultiPartBuilder(boundary: String, private val outputStream: OutputStream) : Closeable {
        private val boundary: ByteArray

        init {
            this.boundary = boundary.toByteArray(StandardCharsets.UTF_8)
        }

        @Throws(IOException::class)
        private fun append(s: String) = append(s.toByteArray(StandardCharsets.UTF_8))

        @Throws(IOException::class)
        private fun append(b: ByteArray): MultiPartBuilder = apply {
            outputStream.write(b)
        }

        /**
         * Append file. Will automatically be encoded for you
         *
         * @param fieldName  The parameter field name
         * @param uploadFile The parameter file
         * @return self
         */
        @Throws(IOException::class)
        public fun appendFile(fieldName: String, uploadFile: File): MultiPartBuilder = apply {
            append(PREFIX).append(boundary).lineFeed()
            append("Content-Disposition: form-data; name=\"").append(fieldName)
                .append("\"; filename=\"").append(uploadFile.getName()).append("\"")
                .lineFeed()
            append("Content-Type: ").append(URLConnection.guessContentTypeFromName(uploadFile.getName()))
                .lineFeed()
            append("Content-Transfer-Encoding: binary").lineFeed()
            lineFeed()
            outputStream.flush()
            FileInputStream(uploadFile).use { inputStream -> pipe(inputStream, outputStream) }
            lineFeed()
            outputStream.flush()
        }

        /**
         * Append InputStream. Will automatically be encoded for you
         *
         * @param fieldName The parameter field name
         * @param is        The parameter stream
         * @return self
         */
        @Throws(IOException::class)
        public fun appendStream(fieldName: String, `is`: InputStream): MultiPartBuilder = apply {
            append(PREFIX).append(boundary).lineFeed()
            append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"")
                .lineFeed()
            append("Content-Transfer-Encoding: binary").lineFeed()
            lineFeed()
            outputStream.flush()
            pipe(`is`, outputStream)
            lineFeed()
            outputStream.flush()
        }

        /**
         * Append field. Will automatically be encoded for you
         *
         * @param fieldName The parameter field name
         * @param value     The parameter value
         * @return self
         */
        @Throws(IOException::class)
        public fun appendField(fieldName: String, value: String): MultiPartBuilder = apply {
            append(PREFIX).append(boundary).lineFeed()
            append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"")
                .lineFeed()
            lineFeed()
            append(value).lineFeed()
            outputStream.flush()
        }

        /**
         * Finishes this MultiPartForm. This should be called last.
         * Calling any other methods on this Builder after calling this will lead to undefined behaviour.
         */
        @Throws(IOException::class)
        public fun finish(): Unit = use {
            append(PREFIX).append(boundary).append(PREFIX).lineFeed()
            outputStream.flush()
        }

        @Throws(IOException::class)
        override public fun close(): Unit = outputStream.close()

        private companion object {
            private const val LINE_FEED = "\r\n"
            private const val PREFIX = "--"

            fun MultiPartBuilder.lineFeed() = append(LINE_FEED)
        }
    }

    /** Request Builder  */
    public class Request @JvmOverloads constructor(url: String?, method: String = "GET") : Closeable {
        /** The connection of this Request  */
        public val conn: HttpURLConnection

        public constructor(builder: QueryBuilder) : this(builder.toString())

        init {
            conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = method.uppercase(Locale.getDefault())
            conn.addRequestProperty("User-Agent", "Zeetcord (https://github.com/zt64/Zeetcord)")
        }

        /**
         * Add a header
         *
         * @param key   the name
         * @param value the value
         * @return self
         */
        public fun setHeader(key: String?, value: String?): Request = apply {
            conn.setRequestProperty(key, value)
        }

        /**
         * Sets the request connection and read timeout
         *
         * @param timeout the timeout, in milliseconds
         * @return self
         */
        public fun setRequestTimeout(timeout: Int): Request = apply {
            conn.connectTimeout = timeout
            conn.readTimeout = timeout
        }

        /**
         * Sets whether redirects should be followed
         *
         * @param follow Whether redirects should be followed
         * @return self
         */
        public fun setFollowRedirects(follow: Boolean): Request = apply {
            conn.instanceFollowRedirects = follow
        }

        /**
         * Execute the request
         *
         * @return A response object
         */
        @Throws(IOException::class)
        public fun execute(): Response = Response(this)

        /**
         * Execute the request with the specified body. May not be used in GET requests.
         *
         * @param body The request body
         * @return Response
         */
        @Throws(IOException::class)
        public fun executeWithBody(body: String): Response {
            if (conn.requestMethod == "GET") throw IOException("Body may not be specified in GET requests")
            return executeWithBody(body.toByteArray())
        }

        /**
         * Execute the request with the specified raw bytes. May not be used in GET requests.
         *
         * @param bytes The request body in raw bytes
         * @return Response
         */
        @Throws(IOException::class)
        public fun executeWithBody(bytes: ByteArray): Response {
            if (conn.requestMethod == "GET") throw IOException("Body may not be specified in GET requests")
            setHeader("Content-Length", bytes.size.toString())
            conn.doOutput = true
            conn.outputStream.use { out ->
                out.write(bytes, 0, bytes.size)
                out.flush()
            }
            return execute()
        }

        /**
         * Execute the request with the specified object as json. May not be used in GET requests.
         *
         * @param body The request body
         * @return Response
         */
        @Throws(IOException::class)
        public fun executeWithJson(body: Any?): Response = executeWithJson(gson, body)

        /**
         * Execute the request with the specified object as json. May not be used in GET requests.
         *
         * @param gson Gson instance
         * @param body The request body
         * @return Response
         */
        @Throws(IOException::class)
        public fun executeWithJson(gson: Gson, body: Any?): Response {
            return setHeader("Content-Type", "application/json").executeWithBody(gson.toJson(body))
        }

        /**
         * Execute the request with the specified object as
         * [url encoded form data](https://url.spec.whatwg.org/#application/x-www-form-urlencoded).
         * May not be used in GET requests.
         *
         * @param params the form data
         * @return Response
         * @throws IOException if an I/O exception occurred
         */
        @Throws(IOException::class)
        public fun executeWithUrlEncodedForm(params: Map<String?, Any?>): Response {
            val qb = QueryBuilder("")
            for ((key, value) in params) qb.append(key, value.toString())
            return setHeader(
                "Content-Type",
                "application/x-www-form-urlencoded"
            ).executeWithBody(qb.toString().substring(1))
        }

        /**
         * Execute the request with the specified object as multipart form-data. May not be used in GET requests.
         *
         *
         * Please note that this will set the [Transfer-Encoding](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Transfer-Encoding) to chunked.
         * Some servers may not support this. To upload un-chunked (will lead to running out of memory when uploading large files), call
         * `executeWithMultipartForm(params, false)`
         *
         * @param params Map of params. These will be converted in the following way:
         *
         *  * File: Append filename and content-type, then append the bytes of the file
         *  * InputStream: Read the stream fully and append the bytes
         *  * Other: Objects.toString() and append
         *
         * @return Response
         * @throws IOException if an I/O exception occurred
         */
        @Throws(IOException::class)
        public fun executeWithMultipartForm(params: Map<String, Any>): Response {
            return executeWithMultipartForm(params, true)
        }

        /**
         * @param doChunkedUploading Whether to upload in chunks. If this is false, a buffer will be allocated to hold the entire
         * multi part form. When uploading large files this way, you will run out of memory. Not every server
         * supports this, also does not support redirects.
         * @see .executeWithMultipartForm
         */
        @Throws(IOException::class)
        public fun executeWithMultipartForm(
            params: Map<String, Any>,
            doChunkedUploading: Boolean
        ): Response {
            if (conn.requestMethod == "GET") throw IOException("MultiPartForm may not be specified in GET requests")
            if (doChunkedUploading) conn.setChunkedStreamingMode(-1)

            val boundary = "--${UUID.randomUUID()}--"

            setHeader("Content-Type", "multipart/form-data; boundary=$boundary")
            conn.doOutput = true

            MultiPartBuilder(boundary, conn.outputStream).use { builder ->
                params.forEach { (key, value) ->
                    when (value) {
                        is File -> builder.appendFile(key, value)
                        is InputStream -> builder.appendStream(key, value)
                        else -> builder.appendField(key, value.toString())
                    }
                }
                builder.finish()
                return execute()
            }
        }

        /** Closes this request  */
        override fun close(): Unit = conn.disconnect()

        public companion object {
            /**
             * Performs a GET request to a Discord route
             *
             * @param builder QueryBuilder
             * @throws IOException If an I/O exception occurs
             */
            @JvmStatic
            @Throws(IOException::class)
            public fun newDiscordRequest(builder: QueryBuilder): Request {
                return newDiscordRequest(builder.toString())
            }

            /**
             * Performs a request to a Discord route
             *
             * @param route  A Discord route, such as `/users/@me`
             * @param method [HTTP method](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods)
             * @throws IOException If an I/O exception occurs
             */
            @JvmStatic
            @JvmOverloads
            @Throws(IOException::class)
            public fun newDiscordRequest(route: String, method: String = "GET"): Request {
                return Request(getDiscordRoute(route), method).apply {
                    val headersProvider = AppHeadersProvider.INSTANCE
                    setHeader("User-Agent", headersProvider.userAgent)
                    setHeader(
                        "X-Super-Properties",
                        AnalyticSuperProperties.INSTANCE.superPropertiesStringBase64
                    )
                    setHeader("Accept", "*/*")
                    setHeader("Authorization", headersProvider.authToken)
                    setHeader("Accept-Language", headersProvider.acceptLanguages)
                    setHeader("X-Discord-Locale", headersProvider.locale)
                }
            }

            /**
             * Performs a GET request to a Discord route using RN headers
             *
             * @param builder QueryBuilder
             * @throws IOException If an I/O exception occurs
             */
            @JvmStatic
            @Throws(IOException::class)
            public fun newDiscordRNRequest(builder: QueryBuilder): Request {
                return newDiscordRNRequest(builder.toString(), "GET")
            }

            /**
             * Performs a request to a Discord route using RN headers
             *
             * @param route  A Discord route, such as `/users/@me`
             * @param method [HTTP method](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods)
             * @throws IOException If an I/O exception occurs
             */
            @JvmStatic
            @JvmOverloads
            @Throws(IOException::class)
            public fun newDiscordRNRequest(route: String, method: String = "GET"): Request {
                return Request(getDiscordRoute(route), method).apply {
                    val headersProvider = AppHeadersProvider.INSTANCE
                    setHeader("User-Agent", RNSuperProperties.USER_AGENT)
                    setHeader("X-Super-Properties", superPropertiesBase64)
                    setHeader("Accept-Language", headersProvider.acceptLanguages)
                    setHeader("Accept", "*/*")
                    setHeader("Authorization", headersProvider.authToken)
                    setHeader("X-Discord-Locale", headersProvider.locale)
                    setHeader("X-Discord-Timezone", TimeZone.getDefault().id)
                }
            }

            private fun getDiscordRoute(route: String): String {
                return if (route.startsWith("http")) route else "https://discord.com/api/v9$route"
            }
        }
    }

    /** Response obtained by calling Request.execute()  */
    public data class Response(private val req: Request) : Closeable {
        /** The [status code](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status) of this response  */
        val statusCode: Int = req.conn.getResponseCode()

        /** The [status message](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status) of this response  */
        val statusMessage: String = req.conn.getResponseMessage()

        /** Whether the request was successful (status code 2xx)  */
        public fun ok(): Boolean = statusCode in 200..299

        /** Throws an HttpException if this request was unsuccessful  */
        @Throws(HttpException::class)
        public fun assertOk() {
            if (!ok()) throw HttpException(req, this)
        }

        /** Get the response body as String  */
        @Throws(IOException::class)
        public fun text(): String = stream().use(::readAsText)

        @get:Throws(IOException::class)
        val bytes: ByteArray
            /** Get the response body as `byte[]`  */
            get() = stream().use(::readBytes)

        /**
         * Deserializes json response
         *
         * @param type Type to deserialize into
         * @return Response Object
         */
        @Throws(IOException::class)
        public fun <T> json(type: Type?): T = json(gson, type)

        /**
         * Deserializes json response
         *
         * @param gson Gson instance
         * @param type Type to deserialize into
         * @return Response Object
         */
        @Throws(IOException::class)
        public fun <T> json(gson: Gson, type: Type?): T = gson.fromJson(text(), type)

        /**
         * Deserializes json response
         *
         * @param type Class to deserialize into
         * @return Response Object
         */
        @Throws(IOException::class)
        public fun <T> json(type: Class<T>): T = json(gson, type)

        /**
         * Deserializes json response
         *
         * @param gson Gson instance
         * @param type Class to deserialize into
         * @return Response Object
         */
        @Throws(IOException::class)
        public fun <T> json(gson: Gson, type: Class<T>): T = gson.fromJson(text(), type)

        /**
         * Get the raw response stream of this connection
         *
         * @return InputStream
         */
        @Throws(IOException::class)
        public fun stream(): InputStream {
            assertOk()
            return req.conn.inputStream
        }

        /**
         * Pipe response into OutputStream. Remember to close the OutputStream
         *
         * @param os The OutputStream to pipe into
         */
        @Throws(IOException::class)
        public fun pipe(os: OutputStream?): Unit = stream().use { `is` -> pipe(`is`, os!!) }

        /**
         * Saves the received data to the specified [File]
         * and verifies its integrity using the specified sha1sum
         *
         * @param file    The file to save the data to
         * @param sha1sum checksum to check the file's integrity. May be null to skip integrity check
         * @throws IOException If an I/O error occurred: No such file, file is directory, integrity check failed, etc
         */
        @JvmOverloads
        @Throws(IOException::class)
        public fun saveToFile(file: File, sha1sum: String? = null) {
            if (file.exists()) {
                if (!file.canWrite()) throw IOException("Cannot write to file: ${file.absolutePath}")
                if (file.isDirectory()) throw IOException("Path already exists and is directory: ${file.absolutePath}")
            }

            val parent = file.getParentFile()
                ?: throw IOException("Only absolute paths are supported.")
            val tempFile = File.createTempFile("download", null, parent)

            try {
                val shouldVerify = sha1sum != null
                val md = if (shouldVerify) MessageDigest.getInstance("SHA-1") else null
                try {
                    if (shouldVerify) DigestInputStream(stream(), md) else stream().use { `is` ->
                        FileOutputStream(tempFile).use { os ->
                            pipe(`is`, os)
                            if (shouldVerify) {
                                val hash = String.format("%040x", BigInteger(1, md!!.digest()))
                                if (!hash.equals(sha1sum, ignoreCase = true)) {
                                    throw IOException("Integrity check failed. Expected $sha1sum, received $hash.")
                                }
                            }
                            if (!tempFile.renameTo(file)) throw IOException("Failed to rename temp file.")
                        }
                    }
                } catch (ex: IOException) {
                    if (tempFile.exists() && !tempFile.delete()) {
                        Main.logger.warn("[HTTP#saveToFile] Failed to clean up temp file ${tempFile.absolutePath}")
                    }
                    throw ex
                }
            } catch (ex: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to retrieve SHA-1 MessageDigest instance", ex)
            }
        }

        /**
         * Closes the [Request] associated with this [Response]
         */
        override fun close(): Unit = req.close()
    }
}
