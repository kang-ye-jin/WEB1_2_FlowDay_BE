package org.example.flowday.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.post.comment.entity.Reply;
import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.xml.stream.events.Comment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String pw;
    private String email;
    @Column(unique = true)
    private String name;
    private String phoneNum;
    private Long partnerId;
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;
    private Role role;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDate dateOfRelationshipStart;
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "member")
    private List<Course> courses;

    @OneToMany(mappedBy = "member")
    private List<Reply> reply;

    @OneToMany(mappedBy = "member")
    private List<Post> posts;
}
