package org.example.flowday.global.advice;

import org.example.flowday.domain.course.course.exception.CourseTaskException;
import org.example.flowday.domain.course.spot.exception.SpotTaskException;
import org.example.flowday.domain.course.vote.exception.VoteTaskException;
import org.example.flowday.domain.course.wish.exception.WishPlaceTaskException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class APIControllerAdvice {

    // 코스 예외 처리
    @ExceptionHandler(CourseTaskException.class)
    public ResponseEntity<Map<String, String>> handleCourseTaskException(CourseTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    // 장소 예외 처리
    @ExceptionHandler(SpotTaskException.class)
    public ResponseEntity<Map<String, String>> handleSpotTaskException(SpotTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    // 투표 예외 처리
    @ExceptionHandler(VoteTaskException.class)
    public ResponseEntity<Map<String, String>> handleVoteTaskException(VoteTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    // 투표 예외 처리
    @ExceptionHandler(WishPlaceTaskException.class)
    public ResponseEntity<Map<String, String>> handleWishPlaceTaskException(WishPlaceTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

}
