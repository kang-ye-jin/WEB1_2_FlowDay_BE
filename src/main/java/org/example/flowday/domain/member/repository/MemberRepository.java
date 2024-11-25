package org.example.flowday.domain.member.repository;

import org.example.flowday.domain.member.dto.MemberDTO;
import org.example.flowday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByLoginId(String username);
    Optional<Member> findByLoginId(String loginId); // 로그인 ID로 검색

    @Query("SELECT " +
            "m1.profileImage AS profileImage, " +
            "m1.name AS name, " +
            "m2.profileImage AS partnerImage, " +
            "m2.name AS partnerName, " +
            "m1.dateOfRelationshipStart, " +
            "m1.dateOfBirth " +
            "FROM Member m1 " +
            "INNER JOIN Member m2 ON m1.partnerId = m2.id " +
            "WHERE m1.id = :id")
    Optional<Map<String, Object>> findMyPageById(@Param("id") Long id);

    @Query("SELECT m.refreshToken FROM Member m WHERE m.loginId = :loginId")
    Optional<String> findRefreshTokenByLoginId(String loginId);

    @Query("SELECT m.id FROM Member m WHERE m.loginId = :loginId")
    Optional<Long> findIdByLoginId(String loginId);

    @Query("SELECT m.id, m.name, m.email, m.phoneNum, m.profileImage FROM Member m WHERE m.name = :name")
    Optional<Member> findByName(String name);

    @Query("SELECT m.loginId FROM Member m WHERE m.email = :email")
    Optional<String> findLoginIdByEmail(String email);
    Optional<Member> findByLoginIdAndEmail(String loginId , String email);

}

