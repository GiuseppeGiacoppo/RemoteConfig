package me.giacoppo.remoteconfig.locals

import junit.framework.TestCase
import me.giacoppo.remoteconfig.core.ResourceLocalRepository
import org.junit.Test
import java.nio.file.Paths

class LocalRepositoryTest: TestCase() {

    val testDirectory = Paths.get("demoTest")
    val resourceName = "test"
    val resourceContent = "testContent"

    val sut: ResourceLocalRepository = StorageResourceLocalRepository(testDirectory.toFile().absolutePath).also {
        it.setResourceName(resourceName)
    }

    @Test
    fun testDefault() {
        sut.storeDefault(resourceContent.byteInputStream())
        assertEquals(resourceContent, sut.getActive()!!.readBytes().decodeToString())
    }

    @Test
    fun testDefaultAndFetch() {
        sut.storeDefault(resourceContent.byteInputStream())
        sut.storeFetched("fetched".byteInputStream())
        assertEquals(resourceContent, sut.getActive()!!.readBytes().decodeToString())
    }

    @Test
    fun testFetch() {
        sut.storeFetched(resourceContent.byteInputStream())
        assertNull(sut.getActive())
    }

    @Test
    fun testActivate() {
        sut.storeDefault("default".byteInputStream())
        sut.storeFetched(resourceContent.byteInputStream())
        sut.activate()

        assertNotNull(sut.getActive())
        assertEquals(resourceContent, sut.getActive()!!.readBytes().decodeToString())
    }

    @Test
    fun testClear() {
        sut.storeFetched(resourceContent.byteInputStream())
        sut.activate()

        assertNotNull(sut.getActive())

        sut.clear()

        assertNull(sut.getActive())
    }

    override fun tearDown() {
        sut.clear()
    }
}