package com.example.ordergrabbingapi.exception;

/**
 * Custom exception for grab order related errors
 */
public class GrabOrderException extends RuntimeException {

    private ErrorCode errorCode;

    public GrabOrderException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GrabOrderException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public enum ErrorCode {
        GRAB_ORDER_NOT_FOUND("Grab order not found"),
        GRAB_NOT_STARTED("Grab activity has not started"),
        GRAB_ENDED("Grab activity has ended"),
        INSUFFICIENT_STOCK("Insufficient stock available"),
        INVALID_PHONE_NUMBER("Invalid phone number format"),
        DUPLICATE_ORDER("You have already placed an order for this grab"),
        STOCK_UPDATE_FAILED("Failed to update stock"),
        ORDER_CREATION_FAILED("Failed to create order");

        private String message;

        ErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
