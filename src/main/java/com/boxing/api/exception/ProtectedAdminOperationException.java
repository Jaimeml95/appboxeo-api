package com.boxing.api.exception;

public class ProtectedAdminOperationException extends RuntimeException {

    public ProtectedAdminOperationException(String message) {
        super(message);
    }
}
