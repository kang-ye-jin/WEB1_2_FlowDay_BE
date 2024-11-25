package org.example.flowday.domain.course.spot.service;

import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SpotServiceTest {

    @Mock
    private SpotRepository spotRepository;

    @InjectMocks
    private SpotService spotService;

    private Spot spot1;
    private Spot spot2;
    private Spot spot3;
    private Spot spot4;
    private Spot spot5;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        spot1 = Spot.builder()
                .id(1L)
                .placeId("ChIJgUbEo1")
                .name("장소1")
                .city("서울")
                .sequence(1)
                .build();

        spot2 = Spot.builder()
                .id(2L)
                .placeId("ChIJgUbEo2")
                .name("장소2")
                .city("서울")
                .sequence(2)
                .build();

        spot3 = Spot.builder()
                .id(3L)
                .placeId("ChIJgUbEo3")
                .name("장소3")
                .city("서울")
                .sequence(3)
                .build();

        spot4 = Spot.builder()
                .id(4L)
                .placeId("ChIJgUbEo4")
                .name("장소4")
                .city("서울")
                .sequence(4)
                .build();

        spot5 = Spot.builder()
                .id(5L)
                .placeId("ChIJgUbEo5")
                .name("장소5")
                .city("서울")
                .sequence(5)
                .build();
    }

    @DisplayName("지역별 인기 장소 Top 4 조회 테스트")
    @Test
    void getTopSpotsByCity() {
        when(spotRepository.findAllByCity("서울")).thenReturn(List.of(spot1, spot2, spot3, spot4, spot5));

        List<SpotResDTO> result = spotService.getTopSpotsByCity("서울");

        assertThat(result).hasSize(4);
        assertThat(result.get(0).getName()).isEqualTo("장소5");
        assertThat(result.get(1).getName()).isEqualTo("장소4");
        assertThat(result.get(2).getName()).isEqualTo("장소3");
        assertThat(result.get(3).getName()).isEqualTo("장소2");

        verify(spotRepository, times(1)).findAllByCity("서울");
    }

    @DisplayName("지역별 인기 장소가 없는 경우 테스트")
    @Test
    void getTopSpotsByCity_noSpots() {
        when(spotRepository.findAllByCity("부산"))
                .thenReturn(List.of());

        List<SpotResDTO> result = spotService.getTopSpotsByCity("부산");

        assertThat(result).isEmpty();

        verify(spotRepository, times(1)).findAllByCity("부산");
    }

}
