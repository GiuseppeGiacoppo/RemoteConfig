package me.giacoppo.remoteconfig.exceptions

import java.io.IOException

/**
 * Wraps the http code and server message in case of not successful response
 */
class HttpException : IOException {
    val httpCode: Int

    constructor(httpCode: Int) : super("[http code: $httpCode]") {
        this.httpCode = httpCode
    }

    constructor(httpCode: Int, message: String) : super("[http code: $httpCode] $message") {
        this.httpCode = httpCode
    }
}
