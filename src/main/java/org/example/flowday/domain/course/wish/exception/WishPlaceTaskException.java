package org.example.flowday.domain.course.wish.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishPlaceTaskException extends RuntimeException {
    private String message;
    private int code;
}
