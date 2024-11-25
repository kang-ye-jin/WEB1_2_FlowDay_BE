package org.example.flowday.domain.course.vote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;

import java.util.List;

@Builder
@Getter
public class VoteReqDTO {
    @NotNull(message = "Course ID cannot be null")
    private Long courseId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private List<SpotReqDTO> spots;
}

