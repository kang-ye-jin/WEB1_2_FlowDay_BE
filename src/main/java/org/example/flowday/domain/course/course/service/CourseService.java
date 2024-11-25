package org.example.flowday.domain.course.course.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.dto.PageReqDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.exception.CourseException;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.exception.SpotException;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final SpotRepository spotRepository;
    private final WishPlaceService wishPlaceService;

    // 코스 생성
    public CourseResDTO saveCourse(CourseReqDTO courseReqDTO) {
        try {
            Member member = memberRepository.findById(courseReqDTO.getMemberId()).orElseThrow(MemberException.MEMBER_EMAIL_NOT_FOUND::getMemberTaskException);
            Course course = Course.builder()
                    .member(member)
                    .title(courseReqDTO.getTitle())
                    .status(courseReqDTO.getStatus())
                    .date(courseReqDTO.getDate())
                    .color(courseReqDTO.getColor())
                    .build();

            courseRepository.save(course);

            List<SpotResDTO> spotResDTOs = new ArrayList<>();

            for (SpotReqDTO spotReqDTO : courseReqDTO.getSpots()) {
                Spot spot = Spot.builder()
                        .placeId(spotReqDTO.getPlaceId())
                        .name(spotReqDTO.getName())
                        .city(spotReqDTO.getCity())
                        .comment(spotReqDTO.getComment())
                        .sequence(spotReqDTO.getSequence())
                        .course(course)
                        .build();

                spotRepository.save(spot);

                spotResDTOs.add(new SpotResDTO(spot));
            }

            CourseResDTO courseResDTO = new CourseResDTO(course, spotResDTOs);

            return courseResDTO;
        } catch (Exception e) {
            throw CourseException.NOT_CREATED.get();
        }
    }

    // 코스 조회
    public CourseResDTO findCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);
        List<Spot> spots = spotRepository.findAllByCourseIdAndVoteIsNull(courseId);
        List<SpotResDTO> spotResDTOs = new ArrayList<>();

        for (Spot spot : spots) {
            spotResDTOs.add(new SpotResDTO(spot));
        }

        return new CourseResDTO(course, spotResDTOs);
    }

    // 코스 수정
    public CourseResDTO updateCourse(Long courseId, CourseReqDTO courseReqDTO) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        try {
            course.changeTitle(courseReqDTO.getTitle());
            course.changeDate(courseReqDTO.getDate());
            course.changeColor(courseReqDTO.getColor());
            course.changeStatus(courseReqDTO.getStatus());

            List<Spot> existingSpots = spotRepository.findAllByCourseIdAndVoteIsNull(courseId);
            List<SpotReqDTO> updatedSpots = courseReqDTO.getSpots();
            List<SpotResDTO> spotResDTOs = new ArrayList<>();

            for (SpotReqDTO spotReqDTO : updatedSpots) {
                if (spotReqDTO.getId() != null) {
                    Spot spot = existingSpots.stream()
                            .filter(s -> s.getId().equals(spotReqDTO.getId()))
                            .findFirst()
                            .orElseThrow(SpotException.NOT_FOUND::get);

                    spot.changePlaceId(spotReqDTO.getPlaceId());
                    spot.changeName(spotReqDTO.getName());
                    spot.changeCity(spotReqDTO.getCity());
                    spot.changeComment(spotReqDTO.getComment());
                    spot.changeSequence(spotReqDTO.getSequence());

                    spotResDTOs.add(new SpotResDTO(spot));

                } else {
                    Spot spot = Spot.builder()
                            .placeId(spotReqDTO.getPlaceId())
                            .name(spotReqDTO.getName())
                            .city(spotReqDTO.getCity())
                            .comment(spotReqDTO.getComment())
                            .sequence(spotReqDTO.getSequence())
                            .course(course)
                            .build();
                    spotRepository.save(spot);
                    spotResDTOs.add(new SpotResDTO(spot));
                }
            }

            // 코스에서 삭제된 spot 처리
            List<Long> updatedSpotIds = updatedSpots.stream()
                    .map(SpotReqDTO::getId)
                    .toList();

            for (Spot existingSpot : existingSpots) {
                if (!updatedSpotIds.contains(existingSpot.getId())) {
                    try {
                        spotRepository.delete(existingSpot);
                    } catch (Exception e) {
                        throw SpotException.NOT_DELETED.get();
                    }
                }
            }

            return new CourseResDTO(course, spotResDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            throw CourseException.NOT_UPDATED.get();
        }
    }

    // 코스 삭제
    public CourseResDTO removeCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        courseRepository.delete(course);

        return new CourseResDTO(course, null);
    }

    // 회원 별 코스 목록 조회
    public List<CourseResDTO> findCourseByMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.MEMBER_EMAIL_NOT_FOUND::getMemberTaskException);
        Long partnerId = member.getPartnerId() != null ? member.getPartnerId() : null;

        List<Course> memberCourses = courseRepository.findAllByMemberId(memberId);
        List<Course> partnerCourses = partnerId != null
                ? courseRepository.findAllByMemberIdAndStatus(partnerId, Status.COUPLE)
                : new ArrayList<>();

        List<Course> combinedCourses = new ArrayList<>();
        combinedCourses.addAll(memberCourses);
        combinedCourses.addAll(partnerCourses);

        List<CourseResDTO> courseResDTOs = combinedCourses.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(course -> {
                    List<Spot> spots = spotRepository.findAllByCourseIdAndVoteIsNull(course.getId());
                    List<SpotResDTO> spotResDTOs = spots.stream()
                            .map(SpotResDTO::new)
                            .toList();

                    return new CourseResDTO(course, spotResDTOs);
                })
                .toList();

        return courseResDTOs;
    }

    // 회원 별 위시 플레이스, 코스 목록 조회
    public Page<Object> findWishPlaceAndCourseListByMember(Long memberId, PageReqDTO pageReqDTO) {
        Pageable pageable = pageReqDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<WishPlaceResDTO> wishPlaceResDTOS = wishPlaceService.getMemberAndPartnerWishPlaces(memberId);
        List<CourseResDTO> courseResDTOS = findCourseByMember(memberId);

        List<Object> combinedCourses = new ArrayList<>();
        combinedCourses.addAll(wishPlaceResDTOS);
        combinedCourses.addAll(courseResDTOS);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedCourses.size());
        List<Object> paginatedList = combinedCourses.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, combinedCourses.size());
    }

    // 연인 관계 해지 시 모든 코스 비공개로 변경
    public void updateCourseListStatusToPrivate(Long memberId) {
        List<Course> courses = courseRepository.findAllByMemberId(memberId);

        for (Course course : courses) {
            if (Status.COUPLE.equals(course.getStatus())) {
                course.changeStatus(Status.PRIVATE);
            }
        }
    }

}
