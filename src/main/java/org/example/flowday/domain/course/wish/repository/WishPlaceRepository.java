package org.example.flowday.domain.course.wish.repository;

import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishPlaceRepository extends JpaRepository<WishPlace, Long> {

    Optional<WishPlace> findByMemberId(Long memberId);
    List<WishPlace> findAllByMemberId(Long memberId);

}
