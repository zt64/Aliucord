package com.aliucord.gradle

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.configurationcache.extensions.get
import org.gradle.internal.logging.progress.ProgressLogger
import org.gradle.internal.logging.progress.ProgressLoggerFactory
import org.gradle.internal.service.ServiceRegistry
import java.io.File
import java.net.URL

fun createProgressLogger(project: Project, loggerCategory: String): ProgressLogger {
    return createProgressLogger((project as ProjectInternal).services, loggerCategory)
}

fun createProgressLogger(services: ServiceRegistry, loggerCategory: String): ProgressLogger {
    val progressLoggerFactory = services.get<ProgressLoggerFactory>()
    return progressLoggerFactory.newOperation(loggerCategory).also { it.description = loggerCategory }
}

fun URL.download(file: File, progressLogger: ProgressLogger) {
    progressLogger.started()

    try {
        val tempFile = File.createTempFile(file.name, ".part", file.parentFile)
        tempFile.deleteOnExit()

        val connection = this.openConnection()
        val sizeText = toLengthText(connection.contentLengthLong)

        connection.getInputStream().use { inputStream ->
            var finished = false
            var processedBytes: Long = 0
            try {
                tempFile.outputStream().use { os ->
                    val buf = ByteArray(1024 * 10) // 10 KiB
                    var read: Int
                    while (inputStream.read(buf).also { read = it } >= 0) {
                        os.write(buf, 0, read)
                        processedBytes += read
                        progressLogger.progress("Downloading discord apk ${toLengthText(processedBytes)}/$sizeText")
                    }
                    os.flush()
                    finished = true
                }
            } finally {
                if (finished) tempFile.renameTo(file) else tempFile.delete()
            }
        }
    } finally {
        progressLogger.completed()
    }
}

private fun toLengthText(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${bytes / 1024} KB"
    bytes < 1024 * 1024 * 1024 -> "%.2f MB".format(bytes / (1024.0 * 1024.0))
    else -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
}
