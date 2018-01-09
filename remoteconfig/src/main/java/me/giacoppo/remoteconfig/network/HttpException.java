package me.giacoppo.remoteconfig.network;

import java.io.IOException;

public final class HttpException extends IOException {
    public HttpException(int httpCode) {
        super("Network error with http code "+httpCode+"");
    }

    public HttpException(int httpCode, String message) {
        super("[http code: "+httpCode+"] "+message);
    }
}
