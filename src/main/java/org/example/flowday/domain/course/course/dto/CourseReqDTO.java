package org.example.flowday.domain.course.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class CourseReqDTO {
    @NotNull(message = "Member ID cannot be null")
    private Long memberId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Status cannot be null")
    private Status status;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotBlank(message = "Color cannot be blank")
    private String color;

    private List<SpotReqDTO> spots;
}
