package org.example.flowday.domain.course.vote.exception;

public enum VoteException {
    NOT_FOUND("VOTE NOT_FOUND", 404),
    NOT_CREATED("VOTE NOT_CREATED", 400),
    NOT_UPDATED("VOTE NOT_UPDATED", 400),
    NOT_DELETED("VOTE NOT_DELETED", 400);

    private VoteTaskException spotTaskException;

    VoteException(String message, int code) {
        spotTaskException = new VoteTaskException(message, code);
    }

    public VoteTaskException get() {
        return spotTaskException;
    }
}
