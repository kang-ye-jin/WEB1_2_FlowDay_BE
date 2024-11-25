package org.example.flowday.domain.course.course.dto;

import lombok.Getter;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CourseResDTO {
    private Long id;
    private Long memberId;
    private String title;
    private Status status;
    private LocalDate date;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SpotResDTO> spots;

    public CourseResDTO(Course course, List<SpotResDTO> spots) {
        this.id = course.getId();
        this.memberId = course.getMember().getId();
        this.title = course.getTitle();
        this.status = course.getStatus();
        this.date = course.getDate();
        this.color = course.getColor();
        this.createdAt = course.getCreatedAt();
        this.updatedAt = course.getUpdatedAt();
        this.spots = spots != null ? spots : new ArrayList<>();
    }

}
