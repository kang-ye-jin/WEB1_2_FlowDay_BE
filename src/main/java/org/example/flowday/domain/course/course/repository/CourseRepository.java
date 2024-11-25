package org.example.flowday.domain.course.course.repository;

import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByMemberId(Long memberId);
    List<Course> findAllByMemberIdAndStatus(Long memberId, Status status);

}
