package org.example.flowday.domain.course.spot.dto;

import lombok.Getter;
import org.example.flowday.domain.course.spot.entity.Spot;

@Getter
public class SpotResDTO {
    private Long id;
    private String placeId;
    private String name;
    private String city;
    private String comment;
    private int sequence;
    private Long courseId;
    private Long voteId;

    public SpotResDTO(Spot spot) {
        this.id = spot.getId();
        this.placeId = spot.getPlaceId();
        this.name = spot.getName();
        this.city = spot.getCity();
        this.comment = spot.getComment();
        this.sequence = spot.getSequence();
        this.courseId = (spot.getCourse() != null) ? spot.getCourse().getId() : null;
        this.voteId = spot.getVote() != null ? spot.getVote().getId() : null;
    }
}
