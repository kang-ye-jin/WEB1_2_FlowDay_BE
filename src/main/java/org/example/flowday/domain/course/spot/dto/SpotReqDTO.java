package org.example.flowday.domain.course.spot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpotReqDTO {
    private Long id;

    @NotBlank(message = "Place Id cannot be blank")
    private String placeId;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "City cannot be blank")
    private String city;

    private String comment;
    private int sequence;
}