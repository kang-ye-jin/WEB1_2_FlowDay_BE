package org.example.flowday.domain.course.vote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.entity.Vote;
import org.example.flowday.domain.course.vote.service.VoteService;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class VoteControllerTest {

    @Mock
    private VoteService voteService;

    @InjectMocks
    private VoteController voteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Member member;
    private Spot spot;
    private Course course;
    private Vote vote;
    private VoteReqDTO voteReqDTO;
    private VoteResDTO voteResDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(voteController).build();
        objectMapper = new ObjectMapper();

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
                .title("Test Course")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .spots(List.of(spot))
                .createdAt(LocalDateTime.now())
                .build();

        vote = Vote.builder()
                .id(1L)
                .course(course)
                .title("뭐먹지")
                .spots(List.of(spot))
                .build();

        voteReqDTO = VoteReqDTO.builder()
                .courseId(1L)
                .title("뭐먹지")
                .spots(Collections.singletonList(
                        SpotReqDTO.builder()
                                .id(1L)
                                .placeId("ChIJgUbEo1")
                                .name("장소 이름1")
                                .city("서울")
                                .sequence(1)
                                .build()
                ))
                .build();

        voteResDTO = new VoteResDTO(
                vote,
                Collections.singletonList(new SpotResDTO(spot))
        );
    }

    @DisplayName("투표 생성 테스트")
    @Test
    void createVote() throws Exception {
        when(voteService.saveVote(any(VoteReqDTO.class))).thenReturn(voteResDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteReqDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("뭐먹지"));
    }

    @DisplayName("투표 조회 테스트")
    @Test
    void getVote() throws Exception {
        // Given
        Long voteId = 1L;

        when(voteService.findVote(voteId)).thenReturn(voteResDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/votes/{voteId}", voteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("뭐먹지"));

        verify(voteService, times(1)).findVote(voteId);
    }

    @DisplayName("투표 완료 후 코스 수정 테스트")
    @Test
    void updateCourseByVote() throws Exception {
        // Given
        Long voteId = 1L;
        Long spotId = 1L;

        // When & Then
        mockMvc.perform(put("/api/v1/votes/{voteId}/spots/{spotId}", voteId, spotId))
                .andExpect(status().isOk());

        verify(voteService, times(1)).updateCourseByVote(voteId, spotId);
    }

}
