package org.example.flowday.domain.course.vote.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteTaskException extends RuntimeException {
    private String message;
    private int code;
}
