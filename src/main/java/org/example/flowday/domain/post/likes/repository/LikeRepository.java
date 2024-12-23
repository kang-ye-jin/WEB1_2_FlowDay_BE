package org.example.flowday.domain.post.likes.repository;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.likes.entity.Likes;
import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByPostIdAndMemberId(Long postId , Long memberId);
    void deleteByMemberIdAndPostId(Long userId, Long postId);

    @Query("SELECT l.postId FROM Likes l WHERE l.memberId = :memberId")
    List<Long> findAllPostIdByMemberId(Long memberId);

}