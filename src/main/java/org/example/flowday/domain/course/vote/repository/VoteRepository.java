package org.example.flowday.domain.course.vote.repository;

import org.example.flowday.domain.course.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
