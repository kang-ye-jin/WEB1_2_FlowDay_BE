package org.example.flowday.domain.course.wish.exception;

public enum WishPlaceException {
    NOT_FOUND("WISHPLACE NOT_FOUND", 404);

    private WishPlaceTaskException wishPlaceTaskException;

    WishPlaceException(String message, int code) {
        wishPlaceTaskException = new WishPlaceTaskException(message, code);
    }

    public WishPlaceTaskException get() {
        return wishPlaceTaskException;
    }
}
