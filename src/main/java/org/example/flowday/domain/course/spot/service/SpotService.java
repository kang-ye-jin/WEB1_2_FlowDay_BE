package org.example.flowday.domain.course.spot.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotService {

    private final SpotRepository spotRepository;

    // 지역별 인기 장소 top4
    public List<SpotResDTO> getTopSpotsByCity(String city) {
        List<Spot> spots = spotRepository.findAllByCity(city);

        if (spots.isEmpty()) {
            return Collections.emptyList();
        }

        // placeId로 그룹화 후 가장 먼저 저장된 장소 선택
        Map<String, Spot> topSpots = spots.stream()
                .collect(Collectors.groupingBy(
                        Spot::getPlaceId,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(Spot::getId)),
                                Optional::get)
                ));

        // 상위 4개 반환
        List<Spot> topSpotsList = topSpots.values().stream()
                .limit(4)
                .collect(Collectors.toList());

        return topSpotsList.stream()
                .map(spot -> new SpotResDTO(spot))
                .collect(Collectors.toList());
    }

}
