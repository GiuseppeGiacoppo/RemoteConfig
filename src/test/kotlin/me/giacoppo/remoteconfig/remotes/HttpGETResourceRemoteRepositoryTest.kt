package me.giacoppo.remoteconfig.remotes

import junit.framework.TestCase
import me.giacoppo.remoteconfig.exceptions.HttpException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch

class HttpGETResourceRemoteRepositoryTest : TestCase() {

    @Test
    fun testError500() {
        val server = MockWebServer()
        val errorCode = 500

        server.enqueue(MockResponse()
            .setResponseCode(errorCode))
        server.start()

        val sut = HttpGETResourceRemoteRepository(server.url(".").toUri().toString())

        val countDownLatch = CountDownLatch(1)
        var stream: InputStream? = null
        var error: Exception? = null

        sut.fetch({
            stream = it
            countDownLatch.countDown()
        }, {
            error = it
            countDownLatch.countDown()
        })

        countDownLatch.await()
        assertNotNull(error)
        assertNull(stream)
        assertTrue(error!! is HttpException)
        assertEquals(errorCode, (error!! as HttpException).httpCode)
    }

    @Test
    fun testErrorTimeout() {
        val server = MockWebServer()
        server.start()

        val sut = HttpGETResourceRemoteRepository(server.url(".").toUri().toString())

        val countDownLatch = CountDownLatch(1)
        var stream: InputStream? = null
        var error: Exception? = null

        sut.fetch({
            stream = it
            countDownLatch.countDown()
        }, {
            error = it
            countDownLatch.countDown()
        })

        countDownLatch.await()
        assertNotNull(error)
        assertNull(stream)
        assertTrue(error!! is IOException)
    }

    @Test
    fun testSuccess() {
        val server = MockWebServer()
        val responseCode = 200
        val responseBody = "This is the response"

        server.enqueue(MockResponse()
            .setResponseCode(responseCode)
            .setBody(responseBody))
        server.start()

        val sut = HttpGETResourceRemoteRepository(server.url(".").toUri().toString())

        val countDownLatch = CountDownLatch(1)
        var stream: InputStream? = null
        var error: Exception? = null

        sut.fetch({
            stream = it
            countDownLatch.countDown()
        }, {
            error = it
            countDownLatch.countDown()
        })

        countDownLatch.await()
        assertNull(error)
        assertNotNull(stream)
        assertEquals(responseBody, stream!!.readBytes().decodeToString())
    }
}
