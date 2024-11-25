package org.example.flowday.domain.course.spot.exception;

public enum SpotException {
    NOT_FOUND("SPOT NOT_FOUND", 404),
    NOT_DELETED("SPOT NOT_DELETED", 400);

    private SpotTaskException spotTaskException;

    SpotException(String message, int code) {
        spotTaskException = new SpotTaskException(message, code);
    }

    public SpotTaskException get() {
        return spotTaskException;
    }
}
