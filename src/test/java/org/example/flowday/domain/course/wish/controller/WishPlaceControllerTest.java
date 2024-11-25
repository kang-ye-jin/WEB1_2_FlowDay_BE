package org.example.flowday.domain.course.wish.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class WishPlaceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WishPlaceService wishPlaceService;

    @InjectMocks
    private WishPlaceController wishPlaceController;

    private ObjectMapper objectMapper;

    private Member member;
    private Member partner;
    private Spot spot;
    private WishPlace wishPlace;
    private WishPlace wishPlace2;
    private WishPlaceReqDTO wishPlaceReqDTO;
    private WishPlaceResDTO wishPlaceResDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(wishPlaceController).build();
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
                .partnerId(2L)
                .build();

        partner = Member.builder()
                .id(2L)
                .loginId("testId2")
                .pw("testPw")
                .email("test2@test.com")
                .name("tester2")
                .phoneNum("010-1234-5678")
                .refreshToken("refresh_token_value")
                .role(Role.ROLE_USER)
                .partnerId(1L)
                .build();

        spot = Spot.builder()
                .id(1L)
                .placeId("ChIJgUbEo1")
                .name("장소 이름1")
                .city("서울")
                .sequence(1)
                .build();

        wishPlace = WishPlace.builder()
                .id(1L)
                .member(member)
                .spots(new ArrayList<>())
                .build();

        wishPlace2 = WishPlace.builder()
                .id(2L)
                .member(partner)
                .spots(List.of(spot))
                .build();

        wishPlaceReqDTO = WishPlaceReqDTO.builder()
                .memberId(1L)
                .spot( SpotReqDTO.builder()
                        .id(1L)
                        .placeId("ChIJgUbEo1")
                        .name("장소 이름1")
                        .city("서울")
                        .sequence(1)
                        .build())
                .build();

        wishPlaceResDTO = new WishPlaceResDTO(
                wishPlace,
                Collections.singletonList(new SpotResDTO(spot))
        );
    }

    @DisplayName("위시 플레이스 장소 추가 테스트")
    @Test
    void addSpotToWishPlace() throws Exception {
        when(wishPlaceService.updateSpotInWishPlace(any(WishPlaceReqDTO.class))).thenReturn(wishPlaceResDTO);

        mockMvc.perform(post("/api/v1/wishPlaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishPlaceReqDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.memberId").value(1L));

        verify(wishPlaceService, times(1)).updateSpotInWishPlace(any());
    }

    @DisplayName("위시 플레이스 장소 삭제 테스트")
    @Test
    void removeSpotFromWishPlace() throws Exception {
        doNothing().when(wishPlaceService).removeSpotFromWishPlace(1L, 1L);

        mockMvc.perform(delete("/api/v1/wishPlaces/member/{memberId}/spot/{spotId}", 1L, 1L))
                .andExpect(status().isNoContent());
    }

    @DisplayName("회원 별 위시 플레이스 목록 조회 테스트")
    @Test
    void getMemberAndPartnerWishPlaces() throws Exception {
        List<WishPlaceResDTO> wishPlaces = new ArrayList<>();
        wishPlaces.add(new WishPlaceResDTO(wishPlace, Collections.emptyList()));
        wishPlaces.add(new WishPlaceResDTO(wishPlace2, Collections.singletonList(new SpotResDTO(spot))));

        when(wishPlaceService.getMemberAndPartnerWishPlaces(1L)).thenReturn(wishPlaces);

        mockMvc.perform(get("/api/v1/wishPlaces/member/{memberId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberId").value(1L))
                .andExpect(jsonPath("$[1].memberId").value(2L));

        verify(wishPlaceService, times(1)).getMemberAndPartnerWishPlaces(1L);
    }

}
