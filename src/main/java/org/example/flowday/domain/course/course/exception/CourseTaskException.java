package org.example.flowday.domain.course.course.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseTaskException extends RuntimeException {
    private String message;
    private int code;
}
