package com.aliucord.gradle.task

import com.aliucord.gradle.getAliucord
import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import jadx.api.impl.NoOpCodeCache
import jadx.api.impl.SimpleCodeWriter
import jadx.plugins.input.dex.DexInputPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.function.Function

abstract class GenSourcesTask : DefaultTask() {
    @TaskAction
    fun genSources() {
        val extension = project.extensions.getAliucord()
        val discord = extension.discord!!

        val sourcesJarFile = discord.cache.resolve("discord-${discord.version}-sources.jar")

        val args = JadxArgs().apply {
            setInputFile(discord.apkFile)
            outDirSrc = sourcesJarFile
            isSkipResources = true
            isShowInconsistentCode = true
            isRespectBytecodeAccModifiers = true
            isFsCaseSensitive = true
            isGenerateKotlinMetadata = false
            isDebugInfo = false
            isInlineAnonymousClasses = false
            isInlineMethods = false
            isReplaceConsts = false

            codeCache = NoOpCodeCache()
            codeWriterProvider = Function(::SimpleCodeWriter)
        }

        JadxDecompiler(args).use { decompiler ->
            decompiler.registerPlugin(DexInputPlugin())
            decompiler.load()
            decompiler.save()
        }
    }
}
