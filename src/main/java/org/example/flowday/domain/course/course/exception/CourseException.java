package org.example.flowday.domain.course.course.exception;

public enum CourseException {
    NOT_FOUND("COURSE NOT_FOUND", 404),
    NOT_CREATED("COURSE NOT_CREATED", 400),
    NOT_UPDATED("COURSE NOT_UPDATED", 400),
    NOT_DELETED("COURSE NOT_DELETED", 400);

    private CourseTaskException courseTaskException;

    CourseException(String message, int code) {
        courseTaskException = new CourseTaskException(message, code);
    }

    public CourseTaskException get() {
        return courseTaskException;
    }
}
