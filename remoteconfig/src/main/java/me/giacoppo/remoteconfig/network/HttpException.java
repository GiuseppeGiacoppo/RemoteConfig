package me.giacoppo.remoteconfig.network;

import java.io.IOException;

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
