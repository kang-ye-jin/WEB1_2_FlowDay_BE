package org.example.flowday.domain.course.wish.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.example.flowday.domain.course.wish.exception.WishPlaceException;
import org.example.flowday.domain.course.wish.repository.WishPlaceRepository;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishPlaceService {

    private final MemberRepository memberRepository;
    private final SpotRepository spotRepository;
    private final WishPlaceRepository wishPlaceRepository;

    // 위시 플레이스 생성
    public void saveWishPlace(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.MEMBER_EMAIL_NOT_FOUND::getMemberTaskException);

        WishPlace wishPlace = WishPlace.builder()
                .member(member)
                .build();

        wishPlaceRepository.save(wishPlace);
    }

    // 위시 플레이스 장소 추가
    public WishPlaceResDTO updateSpotInWishPlace(WishPlaceReqDTO wishPlaceReqDTO) {
        WishPlace wishPlace = wishPlaceRepository.findByMemberId(wishPlaceReqDTO.getMemberId()).orElseThrow(WishPlaceException.NOT_FOUND::get);

        Spot newSpot = Spot.builder()
                .placeId(wishPlaceReqDTO.getSpot().getPlaceId())
                .name(wishPlaceReqDTO.getSpot().getName())
                .city(wishPlaceReqDTO.getSpot().getCity())
                .comment(wishPlaceReqDTO.getSpot().getComment())
                .wishPlace(wishPlace)
                .build();

        spotRepository.save(newSpot);
        wishPlace.getSpots().add(newSpot);

        List<SpotResDTO> spotResDTOs = wishPlace.getSpots().stream()
                .map(spot -> new SpotResDTO(spot))
                .collect(Collectors.toList());

        return new WishPlaceResDTO(wishPlace, spotResDTOs);
    }

    // 위시 플레이스 장소 삭제
    public void removeSpotFromWishPlace(Long memberId, Long spotId) {
        WishPlace wishPlace = wishPlaceRepository.findByMemberId(memberId).orElseThrow(WishPlaceException.NOT_FOUND::get);

        Spot spotToRemove = wishPlace.getSpots().stream()
                .filter(spot -> spot.getId().equals(spotId))
                .findFirst()
                .orElse(null);

        wishPlace.getSpots().remove(spotToRemove);
        spotRepository.delete(spotToRemove);
    }

    // 회원 별 위시 플레이스 목록 조회
    public List<WishPlaceResDTO> getMemberAndPartnerWishPlaces(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.MEMBER_EMAIL_NOT_FOUND::getMemberTaskException);
        Long partnerId = member.getPartnerId();

        List<WishPlace> wishPlaces = new ArrayList<>(wishPlaceRepository.findAllByMemberId(memberId));

        if (partnerId != null) {
            wishPlaces.addAll(wishPlaceRepository.findAllByMemberId(partnerId));
        }

        return wishPlaces.stream()
                .map(wishPlace -> {
                    List<SpotResDTO> spotResDTOs = spotRepository.findAllByWishPlaceId(wishPlace.getId()).stream()
                            .map(SpotResDTO::new)
                            .collect(Collectors.toList());

                    return new WishPlaceResDTO(wishPlace, spotResDTOs);
                })
                .collect(Collectors.toList());
    }

}
