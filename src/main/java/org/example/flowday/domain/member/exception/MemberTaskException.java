package org.example.flowday.domain.member.exception;

import lombok.Getter;

@Getter
public class MemberTaskException extends RuntimeException {

    private final int statusCode;

    public MemberTaskException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
