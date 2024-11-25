package org.example.flowday.domain.course.wish.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;

@Builder
@Getter
public class WishPlaceReqDTO {
    @NotNull(message = "Member cannot be null")
    private Long memberId;

    private SpotReqDTO spot;
}
