package org.example.flowday.domain.member.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.example.flowday.domain.member.dto.MemberDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.exception.MemberTaskException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.global.security.util.JwtUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    private final WishPlaceService wishPlaceService;

    // 보안 관련 서비스


    // refreshToken을 사용하여 새로운 Access Token 생성
    public String refreshAccessToken(String refreshToken) {

        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format");
        }

        String token = refreshToken.split(" ")[1];

        if (jwtUtil.isExpired(token)) {
            throw new IllegalArgumentException("Token expired");
        }

        // refreshToken에서 회원 정보 추출
        Long id = jwtUtil.getId(token);
        String loginId = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 새 accessToken 생성
        return jwtUtil.createJwt(Map.of(
                        "category", "accessToken",
                        "id", id,
                        "loginId", loginId,
                        "role", role),
                60 * 60 * 1000L);
    }

    // 회원 가입
    @Transactional
    public MemberDTO.CreateResponseDTO createMember(Member member) {

        member.setPw(passwordEncoder.encode(member.getPw()));
        member.setRole(Role.ROLE_USER);

        Member savedMember = memberRepository.save(member);

        wishPlaceService.saveWishPlace(savedMember.getId());

        return new MemberDTO.CreateResponseDTO(
                savedMember.getId(),
                savedMember.getLoginId(),
                savedMember.getEmail(),
                savedMember.getName(),
                savedMember.getPhoneNum()
        );

    }

    // 이메일로 아이디 조회
    public MemberDTO.FindIdResponseDTO getMemberByEmail(String email) {

        return new MemberDTO.FindIdResponseDTO(
                memberRepository.findLoginIdByEmail(email)
                        .orElseThrow(
                                MemberException.MEMBER_EMAIL_NOT_FOUND::getMemberTaskException)
        );

    }

    // 임시 비밀번호 생성 및 이메일 발송
    @Transactional
    public void sendTempPasswordEmail(String loginId, String email) throws Exception {

        // 임시 비밀번호 생성
        String templatePassword = setTemplatePassword(loginId, email);

        // 이메일 내용 설정
        String title = "FlowDay 비밀번호 재설정 이메일 입니다.";
        String from = "jy037211@gmail.com";
        String content =
                System.lineSeparator() +
                        System.lineSeparator() +
                        "임시 비밀번호로 로그인 후 꼭 새로운 비밀번호로 설정해주시기 바랍니다." +
                        System.lineSeparator() +
                        System.lineSeparator() +
                        "임시비밀번호는 " + templatePassword + " 입니다. "
                        + System.lineSeparator();

        // 이메일 발송
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setFrom(from);
        messageHelper.setTo(email);
        messageHelper.setSubject(title);
        messageHelper.setText(content);

        mailSender.send(message);
    }
    // 임시 비밀번호 생성 메서드 ( called by sendTempPasswordEmail )
    @Transactional
    public String setTemplatePassword(String loginId, String email) {

        String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

        Member member = memberRepository.findByLoginIdAndEmail(loginId, email).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        SecureRandom random = new SecureRandom();
        StringBuilder tempPassword = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHAR_SET.length());
            tempPassword.append(CHAR_SET.charAt(index));
        }

        String templatePassword = tempPassword.toString();

        member.setPw(passwordEncoder.encode(templatePassword));
        memberRepository.save(member);

        return templatePassword;

    }

    // 로그아웃
    @Transactional
    public void logout (Long id){

        Member member = memberRepository.findById(id).get();

        member.setRefreshToken(null);

        memberRepository.save(member);

    }







    // 내 정보 관련 서비스


    // 마이 페이지에 필요한 정보 가져오기
    public MemberDTO.MyPageResponseDTO getMyPage(Long id) {

        Optional<Map<String,Object>> member = memberRepository.findMyPageById(id);

        if(member.isPresent()) {
            return mapToDTO(member.get());
        } else {
            throw MemberException.MEMBER_NOT_FOUND.getMemberTaskException();
        }

    }
    // Self Join 쿼리로 가져온 데이터를 DTO로 변환 ( called by getMyPage )
    public MemberDTO.MyPageResponseDTO mapToDTO(Map<String, Object> resultMap) {

        String profileImage = (String) resultMap.get("profileImage");
        String name = (String) resultMap.get("name");
        String partnerImage = (String) resultMap.get("partnerImage");
        String partnerName = (String) resultMap.get("partnerName");
        LocalDate dateOfRelationshipStart = (LocalDate) resultMap.get("dateOfRelationshipStart");
        LocalDate dateOfBirth = (LocalDate) resultMap.get("dateOfBirth");

        return new MemberDTO.MyPageResponseDTO(profileImage, name, partnerImage, partnerName, dateOfRelationshipStart, dateOfBirth);

    }

    // 회원 수정
    @Transactional
    public Member updateMember(Long id, Member updatedMember) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (updatedMember.getLoginId() != null && !updatedMember.getLoginId().isEmpty()) {
            member.setLoginId(updatedMember.getLoginId());
        }
        if (updatedMember.getPw() != null && !updatedMember.getPw().isEmpty()) {
            member.setPw(passwordEncoder.encode(updatedMember.getPw()));
        }
        if (updatedMember.getEmail() != null && !updatedMember.getEmail().isEmpty()) {
            member.setEmail(updatedMember.getEmail());
        }
        if (updatedMember.getPhoneNum() != null && !updatedMember.getPhoneNum().isEmpty()) {
            member.setPhoneNum(updatedMember.getPhoneNum());
        }
        if (updatedMember.getName() != null && !updatedMember.getName().isEmpty()) {
            member.setName(updatedMember.getName());
        }

        return memberRepository.save(member);

    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long id) {

        memberRepository.deleteById(id);

    }

    //이미지 변경
    @Transactional
    public MemberDTO.ChangeImageResponseDTO changeProfileImage(Long id, MultipartFile imageFile){

        Optional<Member> memberOp = memberRepository.findById(id);

        if (memberOp.isPresent()) {
            Member member = memberOp.get();
            // 전달 받은 파일 저장 및 이름 추출
            String fileName = saveImage(imageFile);
            // 파일 명 변경
            member.setProfileImage(fileName);

            memberRepository.save(member);

            return new MemberDTO.ChangeImageResponseDTO(member.getId(), imageFile.getOriginalFilename());
        } else {
            throw MemberException.MEMBER_IMAGE_NOT_MODIFIED.getMemberTaskException();
        }

    }

    // 사용자가 보낸 이미지 파일 저장 ( called by changeProfileImage )
    private String saveImage(MultipartFile imageFile) {
        // 기본 파일 저장 경로
        String uploadDir = "upload/";
        try {

            Path uploadPath = Paths.get(uploadDir);

            // 디렉토리가 존재하지 않으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 이름 생성 및 저장
            String ident = String.valueOf(System.currentTimeMillis()).substring(3, 10);
            String fileName = ident + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(imageFile.getInputStream(), filePath); // 실제 파일 저장

            return fileName; // 저장된 파일 이름 반환
        } catch (IOException e) {
            throw MemberException.MEMBER_IMAGE_NOT_SAVED.getMemberTaskException();
        }

    }

    // 생일 변경
    @Transactional
    public void updateBirthday(Long id, MemberDTO.UpdateBirthdayRequestDTO dto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (dto.getDateOfBirth() != null) {
            member.setDateOfBirth(dto.getDateOfBirth());
        }

        memberRepository.save(member);

    }






    // 연인 관련 서비스


    // 커플 등록시 이름으로 ID 조회
    public MemberDTO.FindPartnerResponseDTO getPartner(String name) {

        Optional<Member> member = memberRepository.findByName(name);

        if (member.isPresent()) {
            return new MemberDTO.FindPartnerResponseDTO(member.get());
        } else {
            throw MemberException.MEMBER_NAME_NOT_FOUND.getMemberTaskException();
        }

    }

    // 만나기 시작한 날 등록
    @Transactional
    public void updateRelationshipStartDate(Long id, MemberDTO.UpdateRelationshipStartDateRequestDTO dto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (dto.getDateOfRelationshipStart() != null) {
            member.setDateOfRelationshipStart(dto.getDateOfRelationshipStart());
        }

        memberRepository.save(member);

    }

    // 파트너 ID 등록
    @Transactional
    public void updatePartnerId(Long id, MemberDTO.UpdatePartnerIdRequestDTO dto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (dto.getPartnerId() != null) {
            member.setPartnerId(dto.getPartnerId());
        }

        memberRepository.save(member);

    }

    // 관계 끊기
    @Transactional
    public void disconnectPartner (Long id, Boolean stat){

        Member member = memberRepository.findById(id).get();
        //파트너 Id를 null로 변경
        member.setPartnerId(null);

        if(stat) {
            List<Course> courses = member.getCourses();
            for (Course course : courses) {
                course.setStatus(Status.PRIVATE);
            }
            List<Post> posts = member.getPosts();
            for (Post post : posts) {
                post.setStatus(org.example.flowday.domain.post.post.entity.Status.PRIVATE);
            }
        }

        memberRepository.save(member);

    }






    // 기타 서비스


    // 회원 조회
    public MemberDTO.ReadResponseDTO getMember(Long id) {

        try {
            Member member = memberRepository.findById(id).get();
            return new MemberDTO.ReadResponseDTO(
                    member.getName(),
                    member.getProfileImage(),
                    member.getPartnerId(),
                    member.getDateOfRelationshipStart(),
                    member.getDateOfBirth()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error getting member", e);
        }
    }

}