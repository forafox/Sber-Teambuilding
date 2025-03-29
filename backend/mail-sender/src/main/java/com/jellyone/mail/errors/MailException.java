package com.jellyone.mail.errors;

public class MailException extends ServerError {
    public MailException(String message) {
        super("Error while sending mail: " + message);
    }
}
