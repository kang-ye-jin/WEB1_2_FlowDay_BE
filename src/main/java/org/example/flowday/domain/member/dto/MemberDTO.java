package org.example.flowday.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class MemberDTO {

    @Data
    public static class CreateRequestDTO {
        @NotBlank(message = "로그인 ID는 필수 입력 값 입니다")
        private String loginId;
        @NotBlank(message = "이메일은 필수 입력 값 입니다")
        private String email;
        @NotBlank(message = "비밀번호는 필수 입력 값 입니다")
        private String pw;
        @NotBlank(message = "닉네임은 필수 입력 값 입니다")
        private String name;
        @NotBlank(message = "전화번호는 필수 입력 값 입니다")
        private String phoneNum;

        public Member toEntity() {
            return Member.builder()
                    .loginId(loginId)
                    .email(email)
                    .pw(pw)
                    .name(name)
                    .phoneNum(phoneNum)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    public static class CreateResponseDTO {
        private Long id;
        private String loginId;
        private String email;
        private String name;
        private String phoneNum;
    }

    @Data
    @NoArgsConstructor
    public static class LoginRequestDTO {
        private String loginId;
        private String pw;
    }

    @Data
    public static class UpdateRequestDTO {
        private String loginId;
        private String email;
        private String pw;
        private String name;
        private String phoneNum;

        public Member toEntity() {
            return Member.builder()
                    .loginId(loginId)
                    .email(email)
                    .pw(pw)
                    .name(name)
                    .phoneNum(phoneNum)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    public static class MyPageResponseDTO {
        private String profileImage;
        private String name;
        private String partnerImage;
        private String partnerName;
        private LocalDate dateOfRelationshipStart;
        private LocalDate dateOfBirth;
    }

    @Data
    @AllArgsConstructor
    public static class ReadResponseDTO {
        private String name;
        private String profileImage;
        private Long partnerId;
        private LocalDate dateOfRelationshipStart;
        private LocalDate dateOfBirth;
    }

    @Data
    @AllArgsConstructor
    public static class ChangeImageResponseDTO{
        private Long id;
        private String mImage;
    }

    @Data
    @AllArgsConstructor
    public static class FindIdResponseDTO {
        private String loginId;
    }

    @Data
    public static class FindPWRequestDTO {
        private String loginId;
        private String email;
    }

    @Data
    public static class UpdateBirthdayRequestDTO {
        private LocalDate dateOfBirth;
    }

    @Data
    public static class UpdateRelationshipStartDateRequestDTO {
        private LocalDate dateOfRelationshipStart;
    }

    @Data
    public static class UpdatePartnerIdRequestDTO {
        private Long partnerId;
    }

    @Data
    public static class FindPartnerResponseDTO {
        private String profileImage;
        private String name;
        private String email;
        private String phoneNum;

        public FindPartnerResponseDTO(Member member) {
            this.name = member.getName();
            this.email = member.getEmail();
            this.phoneNum = member.getPhoneNum();
            this.profileImage = member.getProfileImage();
        }
    }
}
