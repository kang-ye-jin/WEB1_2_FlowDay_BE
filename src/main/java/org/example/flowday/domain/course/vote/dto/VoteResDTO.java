package org.example.flowday.domain.course.vote.dto;

import lombok.Getter;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.vote.entity.Vote;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VoteResDTO {
    private Long id;
    private Long courseId;
    private String title;
    private List<SpotResDTO> spots;

    public VoteResDTO(Vote vote, List<SpotResDTO> spots) {
        this.id = vote.getId();
        this.courseId = vote.getCourse().getId();
        this.title = vote.getTitle();
        this.spots = spots != null ? spots : new ArrayList<>();
    }
}

