package org.example.flowday.domain.course.spot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpotTaskException extends RuntimeException {
    private String message;
    private int code;
}
