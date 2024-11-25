package org.example.flowday.domain.course.spot.repository;

import org.example.flowday.domain.course.spot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    List<Spot> findAllByCourseIdAndVoteIsNull(Long courseId);
    List<Spot> findAllByVoteId(Long voteId);
    List<Spot> findAllByCity(String city);
    List<Spot> findAllByWishPlaceId(Long wishPlaceId);
}
