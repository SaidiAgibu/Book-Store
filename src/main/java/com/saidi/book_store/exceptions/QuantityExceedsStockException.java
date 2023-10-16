package com.saidi.book_store.exceptions;

public class QuantityExceedsStockException extends Exception{
    public QuantityExceedsStockException() {
        super();
    }

    public QuantityExceedsStockException(String message) {
        super(message);
    }

    public QuantityExceedsStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuantityExceedsStockException(Throwable cause) {
        super(cause);
    }

    protected QuantityExceedsStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
