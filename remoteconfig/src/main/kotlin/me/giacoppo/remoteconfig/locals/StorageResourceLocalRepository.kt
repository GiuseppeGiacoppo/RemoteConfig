package me.giacoppo.remoteconfig.locals

import me.giacoppo.remoteconfig.core.ResourceLocalRepository
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class StorageResourceLocalRepository(
    rootDir: String
) : ResourceLocalRepository {
    private val root: File = File(rootDir).also {
        if (!it.exists()) {
            it.mkdir()
        }
    }

    private lateinit var resourceName: String

    override fun setResourceName(resourceName: String) {
        this.resourceName = resourceName
    }

    override fun isFetchedFresh(maxAgeInMillis: Long): Boolean {
        val lastFetched = getResourceFile(FETCHED)?.lastModified() ?: 0
        return (System.currentTimeMillis() - lastFetched) <= maxAgeInMillis
    }

    override fun getActive(): InputStream? = getInputStream(ACTIVE)

    override fun storeDefault(defaultValue: InputStream) {
        writeResourceFile(DEFAULT, defaultValue)
        if (Files.notExists(getResourcePath(ACTIVE))) {
            writeResourceFile(ACTIVE, getInputStream(DEFAULT))
        }
    }

    override fun storeFetched(fetchedResource: InputStream) {
        writeResourceFile(FETCHED, fetchedResource)
    }

    override fun activate() {
        writeResourceFile(ACTIVE, getInputStream(FETCHED))
    }

    override fun clear() {
        Files.deleteIfExists(getResourcePath(ACTIVE))
        Files.deleteIfExists(getResourcePath(FETCHED))
        Files.deleteIfExists(getResourcePath(DEFAULT))
    }

    private fun getResourcePath(variant: String): Path {
        return root.resolve(getFileName(variant)).toPath()
    }

    private fun getResourceFile(variant: String): File? {
        return root.resolve(getFileName(variant)).let {
            if (it.exists()) {
                it
            } else {
                null
            }
        }
    }

    private fun getInputStream(variant: String): InputStream? {
        val path = getResourcePath(variant)
        return if (Files.exists(path)) {
            Files.newInputStream(getResourcePath(variant))
        } else {
            null
        }
    }

    private fun writeResourceFile(variant: String, stream: InputStream?) {
        stream?.use {
            Files.write(getResourcePath(variant), it.readBytes())
        }
    }

    private fun getFileName(variant: String) = "${resourceName}_${variant}"

    private companion object Variants {
        private const val ACTIVE = "active"
        private const val FETCHED = "fetched"
        private const val DEFAULT = "default"
    }
}
