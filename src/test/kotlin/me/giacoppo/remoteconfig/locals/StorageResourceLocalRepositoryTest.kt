package me.giacoppo.remoteconfig.locals

import junit.framework.TestCase
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class StorageResourceLocalRepositoryTest : TestCase() {

    val testDirectory = Paths.get("demoTest")
    val resourceName = "test"
    val resourceContent = "testContent"

    @Test
    fun testCreateDefault() {
        val sut = StorageResourceLocalRepository(testDirectory.toFile().absolutePath)
        sut.setResourceName(resourceName)

        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_default")))
        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_active")))

        sut.storeDefault("default".byteInputStream())
        assertTrue(Files.exists(testDirectory.resolve("${resourceName}_default")))
        assertTrue(Files.exists(testDirectory.resolve("${resourceName}_active")))
    }

    @Test
    fun testFetch() {
        val sut = StorageResourceLocalRepository(testDirectory.toFile().absolutePath)
        sut.setResourceName(resourceName)

        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_fetched")))

        sut.storeFetched(resourceContent.byteInputStream())

        assertTrue(Files.exists(testDirectory.resolve("${resourceName}_fetched")))
        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_active")))
    }

    @Test
    fun testActivate() {
        val sut = StorageResourceLocalRepository(testDirectory.toFile().absolutePath)
        sut.setResourceName(resourceName)
        sut.storeFetched(resourceContent.byteInputStream())

        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_active")))

        sut.activate()

        assertTrue(Files.exists(testDirectory.resolve("${resourceName}_active")))
    }

    @Test
    fun testClear() {
        val sut = StorageResourceLocalRepository(testDirectory.toFile().absolutePath)
        sut.setResourceName(resourceName)
        sut.storeDefault("default".byteInputStream())
        sut.storeFetched(resourceContent.byteInputStream())
        sut.activate()
        sut.clear()

        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_default")))
        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_fetched")))
        assertFalse(Files.exists(testDirectory.resolve("${resourceName}_active")))
    }

    override fun tearDown() {
        testDirectory.toFile().deleteRecursively()
    }
}
