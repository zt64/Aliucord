package com.aliucord.gradle.task

import com.android.apksig.ApkSigner
import com.android.builder.internal.packaging.ApkFlinger
import com.android.tools.build.apkzlib.zfile.ApkCreatorFactory
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import java.io.File
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.Date
import java.util.Locale

abstract class SignApkTask : DefaultTask() {
    @get:InputFile
    abstract val inputApk: RegularFileProperty

    @get:OutputFile
    abstract val outputApk: RegularFileProperty

    private val password = "password".toCharArray()

    @TaskAction
    fun signApk() {
        val creationData = ApkCreatorFactory.CreationData.builder()
            .setApkPath(inputApk.get().asFile)
            .build()
        val flinger = ApkFlinger(
            creationData = creationData,
            compressionLevel = 0
        )

        ApkSigner.Builder(listOf(signerConfig))
            .setV1SigningEnabled(false) // TODO: enable so api <24 devices can work, however zip-alignment breaks
            .setV2SigningEnabled(true)
            .setV3SigningEnabled(true)
            .setInputApk(inputApk.get().asFile)
            .setOutputApk(outputApk.get().asFile)
            .build()
            .sign()
    }

    private val signerConfig: ApkSigner.SignerConfig by lazy {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())

        project.layout.buildDirectory.file("ks.keystore").get().asFile.also {
            if (!it.exists()) newKeystore(it)
        }.inputStream().use { stream ->
            keyStore.load(stream, null)
        }

        val alias = keyStore.aliases().nextElement()
        val certificate = keyStore.getCertificate(alias) as X509Certificate

        ApkSigner.SignerConfig.Builder(
            "Zeetcord signer",
            keyStore.getKey(alias, password) as PrivateKey,
            listOf(certificate)
        ).build()
    }

    private fun newKeystore(out: File?) {
        val key = createKey()

        with(KeyStore.getInstance(KeyStore.getDefaultType())) {
            load(null, password)
            setKeyEntry("alias", key.privateKey, password, arrayOf<Certificate>(key.publicKey))
            store(out?.outputStream(), password)
        }
    }

    private fun createKey(): KeySet {
        var serialNumber: BigInteger

        do serialNumber = SecureRandom().nextInt().toBigInteger()
        while (serialNumber < BigInteger.ZERO)

        val x500Name = X500Name("CN=Aliucord Manager")
        val pair = KeyPairGenerator.getInstance("RSA").run {
            initialize(2048)
            generateKeyPair()
        }
        val builder = X509v3CertificateBuilder(
            /* issuer = */ x500Name,
            /* serial = */ serialNumber,
            /* notBefore = */ Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 30L),
            /* notAfter = */ Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 366L * 30L),
            /* dateLocale = */ Locale.ENGLISH,
            /* subject = */ x500Name,
            /* publicKeyInfo = */ SubjectPublicKeyInfo.getInstance(pair.public.encoded)
        )
        val signer = JcaContentSignerBuilder("SHA1withRSA").build(pair.private)

        return KeySet(JcaX509CertificateConverter().getCertificate(builder.build(signer)), pair.private)
    }

    private class KeySet(val publicKey: X509Certificate, val privateKey: PrivateKey)
}
