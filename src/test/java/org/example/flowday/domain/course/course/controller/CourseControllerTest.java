package org.example.flowday.domain.course.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.dto.PageReqDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.service.CourseService;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private ObjectMapper objectMapper;

    private Member member;
    private Spot spot;
    private Course course;
    private CourseReqDTO courseReqDTO;
    private CourseResDTO courseResDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .spots(List.of(spot))
                .createdAt(LocalDateTime.now())
                .build();

        courseReqDTO = CourseReqDTO.builder()
                .memberId(1L)
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
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

        courseResDTO = new CourseResDTO(
                course,
                Collections.singletonList(new SpotResDTO(spot))
        );
    }

    @DisplayName("코스 생성 테스트")
    @Test
    void createCourse() throws Exception {
        when(courseService.saveCourse(any(CourseReqDTO.class))).thenReturn(courseResDTO);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReqDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("코스 이름"))
                .andExpect(jsonPath("$.status").value("COUPLE"))
                .andExpect(jsonPath("$.color").value("blue"));
    }

    @DisplayName("코스 조회 테스트")
    @Test
    void getCourse() throws Exception {
        when(courseService.findCourse(1L)).thenReturn(courseResDTO);

        mockMvc.perform(get("/api/v1/courses/{courseId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("코스 이름"))
                .andExpect(jsonPath("$.status").value("COUPLE"))
                .andExpect(jsonPath("$.color").value("blue"));
    }

    @DisplayName("코스 수정 테스트")
    @Test
    void updateCourse() throws Exception {
        Spot spot2 = Spot.builder()
                .id(2L)
                .placeId("ChIJgUbEo2")
                .name("장소 이름2")
                .city("서울")
                .sequence(1)
                .build();

        course = Course.builder()
                .id(1L)
                .member(member)
                .title("수정 코스")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("pink")
                .spots(List.of(spot, spot2))
                .createdAt(LocalDateTime.now())
                .build();

        courseReqDTO = CourseReqDTO.builder()
                .memberId(1L)
                .title("수정 코스")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("pink")
                .spots(List.of(
                        SpotReqDTO.builder()
                                .id(1L)
                                .placeId("ChIJgUbEo1")
                                .name("장소 이름2")
                                .city("서울")
                                .sequence(1)
                                .build(),
                        SpotReqDTO.builder()
                                .placeId("ChIJgUbEo2")
                                .name("장소 이름2")
                                .city("서울")
                                .sequence(2)
                                .build()
                ))
                .build();

        courseResDTO = new CourseResDTO(
                course,
                List.of(new SpotResDTO(spot), new SpotResDTO(spot2))
        );

        when(courseService.updateCourse(anyLong(), any(CourseReqDTO.class))).thenReturn(courseResDTO);

        mockMvc.perform(put("/api/v1/courses/{courseId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReqDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("수정 코스"))
                .andExpect(jsonPath("$.status").value("COUPLE"))
                .andExpect(jsonPath("$.color").value("pink"))
                .andExpect(jsonPath("$.spots[0].id").value(1L))
                .andExpect(jsonPath("$.spots[0].name").value("장소 이름1"))
                .andExpect(jsonPath("$.spots[1].id").value(2L))
                .andExpect(jsonPath("$.spots[1].name").value("장소 이름2"));
    }

    @DisplayName("코스 삭제 테스트")
    @Test
    void deleteCourse() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/{courseId}", 1L))
                .andExpect(status().isNoContent());
    }

    // 회원 별 위시 플레이스 및 코스 목록 조회 테스트
    @Test
    void getCourseListByMember() throws Exception {
        Long memberId = 1L;
        PageReqDTO pageReqDTO = PageReqDTO.builder().page(1).size(10).build();

        when(courseService.findWishPlaceAndCourseListByMember(memberId, pageReqDTO))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/courses/member/{memberId}", memberId)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

}
