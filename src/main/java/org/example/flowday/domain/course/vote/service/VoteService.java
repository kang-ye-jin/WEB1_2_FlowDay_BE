package org.example.flowday.domain.course.vote.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.exception.CourseException;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.exception.SpotException;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.entity.Vote;
import org.example.flowday.domain.course.vote.exception.VoteException;
import org.example.flowday.domain.course.vote.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final CourseRepository courseRepository;
    private final SpotRepository spotRepository;
    private final VoteRepository voteRepository;

    // 투표 생성
    public VoteResDTO saveVote(VoteReqDTO voteReqDTO) {
        try {
            Course course = courseRepository.findById(voteReqDTO.getCourseId()).orElseThrow(CourseException.NOT_FOUND::get);

            Vote vote = Vote.builder()
                    .course(course)
                    .title(voteReqDTO.getTitle())
                    .build();

            voteRepository.save(vote);

            List<SpotResDTO> spotResDTOs = new ArrayList<>();

            for (SpotReqDTO spotReqDTO : voteReqDTO.getSpots()) {
                Spot spot = Spot.builder()
                        .placeId(spotReqDTO.getPlaceId())
                        .name(spotReqDTO.getName())
                        .city(spotReqDTO.getCity())
                        .comment(spotReqDTO.getComment())
                        .sequence(spotReqDTO.getSequence())
                        .course(course)
                        .vote(vote)
                        .build();

                spotRepository.save(spot);

                spotResDTOs.add(new SpotResDTO(spot));
            }

            VoteResDTO voteResDTO = new VoteResDTO(vote, spotResDTOs);

            return voteResDTO;
        } catch (Exception e) {
            throw VoteException.NOT_CREATED.get();
        }
    }

    // 투표 조회
    public VoteResDTO findVote(Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(VoteException.NOT_FOUND::get);
        List<Spot> spots = spotRepository.findAllByVoteId(vote.getId());

        List<SpotResDTO> spotResDTOs = new ArrayList<>();
        for (Spot spot : spots) {
            spotResDTOs.add(new SpotResDTO(spot));
        }

        return new VoteResDTO(vote, spotResDTOs);
    }

    // 투표 완료 후 코스 수정
    @Transactional
    public CourseResDTO updateCourseByVote(Long voteId, Long spotId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(VoteException.NOT_FOUND::get);
        Spot spot = spotRepository.findById(spotId).orElseThrow(SpotException.NOT_FOUND::get);
        Course course = vote.getCourse();

        // 선택한 장소 투표 연관 관계 제거
        if (spot.getVote() != null && spot.getVote().getId().equals(voteId)) {
            spot.removeVote();
            spotRepository.save(spot);
        }

        // 선택하지 않은 장소들 삭제
        List<Spot> spotsToDelete = spotRepository.findAllByVoteId(vote.getId());
        for (Spot spotToDelete : spotsToDelete) {
            spotToDelete.removeVote();
            spotRepository.save(spotToDelete);
            spotRepository.delete(spotToDelete);
            spotRepository.flush();
        }

        voteRepository.delete(vote);

        List<SpotResDTO> spotResDTOs = course.getSpots().stream()
                .map(SpotResDTO::new)
                .collect(Collectors.toList());

        return new CourseResDTO(course, spotResDTOs);
    }

}
