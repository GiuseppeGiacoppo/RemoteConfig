package me.giacoppo.remoteconfig.exceptions;

import java.io.IOException;

/**
 * Wraps the http code and server message in case of not successful response
 */
public final class HttpException extends IOException {
    private final int httpCode;

    public HttpException(int httpCode) {
        super("[http code: " + httpCode + "]");
        this.httpCode = httpCode;
    }

    public HttpException(int httpCode, String message) {
        super("[http code: " + httpCode + "] " + message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
