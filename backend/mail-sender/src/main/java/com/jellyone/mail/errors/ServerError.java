package com.jellyone.mail.errors;

public class ServerError extends RuntimeException {
    public ServerError(String message) {
        super("Error on server side: " + message);
    }
}
