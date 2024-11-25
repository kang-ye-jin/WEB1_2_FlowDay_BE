package org.example.flowday.domain.course.course.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.dto.PageReqDTO;
import org.example.flowday.domain.course.course.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    // 코스 생성
    @PostMapping
    public ResponseEntity<CourseResDTO> createCourse(@RequestBody CourseReqDTO courseReqDTO) {
        return ResponseEntity.ok(courseService.saveCourse(courseReqDTO));
    }

    // 코스 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResDTO> getCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.findCourse(courseId));
    }

    // 코스 수정
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResDTO> updateCourse(@PathVariable Long courseId, @RequestBody CourseReqDTO courseReqDTO) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, courseReqDTO));
    }

    // 코스 삭제
    @DeleteMapping("/{course_id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("course_id") Long courseId) {
        courseService.removeCourse(courseId);

        return ResponseEntity.noContent().build();
    }

    // 회원 별 위시 플레이스, 코스 목록 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<Object>> CourseListByMember(
            @PathVariable("memberId") Long memberId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        PageReqDTO pageReqDTO = PageReqDTO.builder().page(page).size(size).build();
        return ResponseEntity.ok(courseService.findWishPlaceAndCourseListByMember(memberId, pageReqDTO));
    }

}
