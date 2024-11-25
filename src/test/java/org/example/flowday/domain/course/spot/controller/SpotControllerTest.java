package org.example.flowday.domain.course.spot.controller;

import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.service.SpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class SpotControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SpotService spotService;

    @InjectMocks
    private SpotController spotController;

    private Spot spot1;
    private Spot spot2;
    private Spot spot3;
    private Spot spot4;
    private Spot spot5;
    private SpotResDTO spotResDTO1;
    private SpotResDTO spotResDTO2;
    private SpotResDTO spotResDTO3;
    private SpotResDTO spotResDTO4;
    private SpotResDTO spotResDTO5;

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

        spotResDTO1 = new SpotResDTO(spot1);
        spotResDTO2 = new SpotResDTO(spot2);
        spotResDTO3 = new SpotResDTO(spot3);
        spotResDTO4 = new SpotResDTO(spot4);
        spotResDTO5 = new SpotResDTO(spot5);

        mockMvc = MockMvcBuilders.standaloneSetup(spotController).build();
    }

    @DisplayName("지역별 인기 장소 Top 4 조회 테스트")
    @Test
    void getTopSpotsByCity() throws Exception {
        when(spotService.getTopSpotsByCity("서울")).thenReturn(List.of(spotResDTO1, spotResDTO2, spotResDTO3, spotResDTO4, spotResDTO5));

        mockMvc.perform(get("/api/v1/spots/top4")
                        .param("city", "서울")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("장소1"))
                .andExpect(jsonPath("$[1].name").value("장소2"))
                .andExpect(jsonPath("$[2].name").value("장소3"))
                .andExpect(jsonPath("$[3].name").value("장소4"));
    }

    @DisplayName("지역별 인기 장소가 없는 경우 테스트")
    @Test
    void getTopSpotsByCity_noSpots() throws Exception {
        when(spotService.getTopSpotsByCity("부산"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/spots/top4")
                        .param("city", "부산")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

}
