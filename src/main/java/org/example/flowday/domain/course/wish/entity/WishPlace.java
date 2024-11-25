package org.example.flowday.domain.course.wish.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.member.entity.Member;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @OneToMany(mappedBy = "wishPlace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spot> spots;
}
