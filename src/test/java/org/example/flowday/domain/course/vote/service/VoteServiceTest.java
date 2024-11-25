package org.example.flowday.domain.course.vote.service;

import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.entity.Vote;
import org.example.flowday.domain.course.vote.repository.VoteRepository;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VoteServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private VoteService voteService;

    private Member member;
    private Spot spot;
    private Course course;
    private Vote vote;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .id(1L)
                .loginId("testId")
                .pw("testPw")
                .email("test@test.com")
                .name("tester")
                .phoneNum("010-1234-5678")
                .refreshToken("refresh_token_value")
                .role(Role.ROLE_USER)
                .build();

        spot = Spot.builder()
                .id(1L)
                .placeId("ChIJgUbEo1")
                .name("장소 이름1")
                .city("서울")
                .sequence(1)
                .build();

        course = Course.builder()
                .id(1L)
                .member(member)
                .title("코스 이름")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .spots(List.of(spot))
                .createdAt(LocalDateTime.now())
                .build();

        vote = Vote.builder()
                .id(1L)
                .course(course)
                .title("뭐먹지")
                .spots(List.of(spot))
                .build();
    }

    @DisplayName("투표 생성 테스트")
    @Test
    void saveVote() {
        VoteReqDTO voteReqDTO = VoteReqDTO.builder()
                .courseId(1L)
                .title("뭐먹지")
                .spots(List.of(
                        SpotReqDTO.builder().id(1L).placeId("ChIJgUbEo1").name("장소 이름1").city("서울").sequence(1).build()
                ))
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(spotRepository.saveAll(anyList())).thenReturn(List.of(spot));
        when(voteRepository.save(any(Vote.class))).thenReturn(vote);

        VoteResDTO result = voteService.saveVote(voteReqDTO);

        assertNotNull(result);
        assertEquals("뭐먹지", result.getTitle());
        verify(courseRepository).findById(1L);
        verify(voteRepository).save(any(Vote.class));
        verify(spotRepository, times(1)).save(any(Spot.class));
    }

    @DisplayName("투표 조회 테스트")
    @Test
    void findVote() {
        when(voteRepository.findById(1L)).thenReturn(Optional.of(vote));
        when(spotRepository.findAllByVoteId(1L)).thenReturn(List.of(spot));

        VoteResDTO result = voteService.findVote(1L);

        assertNotNull(result);
        assertEquals("뭐먹지", result.getTitle());
        verify(voteRepository).findById(1L);
        verify(spotRepository).findAllByVoteId(1L);
    }
    @DisplayName("투표 완료 후 코스 수정 테스트")
    @Test
    void updateCourseByVote() {
        List<Spot> spots = Arrays.asList(spot);
        course.setSpots(spots);

        when(voteRepository.findById(1L)).thenReturn(Optional.of(vote));
        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(spotRepository.findAllByVoteId(1L)).thenReturn(spots);
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        assertDoesNotThrow(() -> {
            voteService.updateCourseByVote(1L, 1L);
        });

        verify(spotRepository).findById(1L);
        verify(voteRepository).delete(any(Vote.class));
        verify(spotRepository).delete(any(Spot.class));
    }

}
