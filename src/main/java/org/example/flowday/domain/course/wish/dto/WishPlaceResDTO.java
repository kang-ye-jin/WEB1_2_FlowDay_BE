package org.example.flowday.domain.course.wish.dto;

import lombok.Getter;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WishPlaceResDTO {
    private Long id;
    private Long memberId;
    private List<SpotResDTO> spots;

    public WishPlaceResDTO(WishPlace wishPlace, List<SpotResDTO> spots) {
        this.id = wishPlace.getId();
        this.memberId = wishPlace.getMember().getId();
        this.spots = spots != null ? spots : new ArrayList<>();
    }
}
