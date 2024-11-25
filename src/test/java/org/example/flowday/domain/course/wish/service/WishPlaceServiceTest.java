package org.example.flowday.domain.course.wish.service;

import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.example.flowday.domain.course.wish.repository.WishPlaceRepository;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishPlaceServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private WishPlaceRepository wishPlaceRepository;

    @InjectMocks
    private WishPlaceService wishPlaceService;

    private Member member;
    private Member partner;
    private Spot spot;
    private WishPlace wishPlace;
    private WishPlace wishPlace2;

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
    }

    @DisplayName("위시 플레이스 생성 테스트")
    @Test
    void saveWishPlace() {
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        wishPlaceService.saveWishPlace(memberId);

        verify(wishPlaceRepository, times(1)).save(any(WishPlace.class));
    }

    @DisplayName("위시 플레이스 장소 추가 테스트")
    @Test
    void updateSpotInWishPlace() {
        when(wishPlaceRepository.findByMemberId(1L)).thenReturn(Optional.of(wishPlace));

        WishPlaceReqDTO wishPlaceReqDTO = WishPlaceReqDTO.builder()
                .memberId(1L)
                .spot(SpotReqDTO.builder()
                        .placeId("ChIJgUbEo2")
                        .name("장소 이름2")
                        .city("서울")
                        .sequence(2)
                        .build())
                .build();

        when(wishPlaceRepository.findByMemberId(1L)).thenReturn(Optional.of(wishPlace));

        WishPlaceResDTO result = wishPlaceService.updateSpotInWishPlace(wishPlaceReqDTO);

        // then
        assertNotNull(result);
        verify(spotRepository, times(1)).save(any(Spot.class));
    }

    @DisplayName("위시 플레이스 장소 삭제 테스트")
    @Test
    void removeSpotFromWishPlace() {
        wishPlace.getSpots().add(spot);

        when(wishPlaceRepository.findByMemberId(1L)).thenReturn(Optional.of(wishPlace));

        wishPlaceService.removeSpotFromWishPlace(1L, 1L);

        assertTrue(wishPlace.getSpots().isEmpty());
        verify(spotRepository, times(1)).delete(spot);
    }

    @DisplayName("회원 별 위시 플레이스 목록 조회 테스트")
    @Test
    void getMemberAndPartnerWishPlaces() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(wishPlaceRepository.findAllByMemberId(1L)).thenReturn(List.of(wishPlace));
        when(wishPlaceRepository.findAllByMemberId(2L)).thenReturn(List.of(wishPlace2));

        List<WishPlaceResDTO> result = wishPlaceService.getMemberAndPartnerWishPlaces(1L);

        assertNotNull(result);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.get(1).getMemberId()).isEqualTo(2L);
    }

}
