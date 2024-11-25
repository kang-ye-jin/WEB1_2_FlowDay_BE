package org.example.flowday.domain.course.spot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.vote.entity.Vote;
import org.example.flowday.domain.course.wish.entity.WishPlace;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeId;
    private String name;
    private String city;
    private String comment;
    private int sequence;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "vote_id", nullable = true)
    private Vote vote;

    @ManyToOne
    @JoinColumn(name = "wish_place_id", nullable = true)
    private WishPlace wishPlace;

    public void changePlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeCity(String city) {
        this.city = city;
    }

    public void changeComment(String comment) {
        this.comment = comment;
    }

    public void changeSequence(int sequence) {
        this.sequence = sequence;
    }

    public void removeVote() {
        this.vote = null;
    }
}

