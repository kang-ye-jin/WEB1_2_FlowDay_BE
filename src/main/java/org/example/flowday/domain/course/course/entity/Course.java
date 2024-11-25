package org.example.flowday.domain.course.course.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    private String title;
    private LocalDate date;
    private String color;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spot> spots;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeDate(LocalDate date) {
        this.date = date;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

}
