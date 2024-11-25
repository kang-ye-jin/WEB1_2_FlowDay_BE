package org.example.flowday.domain.course.vote.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
public class VoteController {

    private final VoteService voteService;

    // 투표 생성
    @PostMapping
    public ResponseEntity<VoteResDTO> createVote(@RequestBody VoteReqDTO voteReqDTO) {
        return ResponseEntity.ok(voteService.saveVote(voteReqDTO));
    }

    // 투표 조회
    @GetMapping("/{voteId}")
    public ResponseEntity<VoteResDTO> getVote(@PathVariable Long voteId) {
        return ResponseEntity.ok(voteService.findVote(voteId));
    }

    // 투표 완료 후 코스 수정
    @PutMapping("/{voteId}/spots/{spotId}")
    public ResponseEntity<CourseResDTO> updateCourseByVote(@PathVariable Long voteId, @PathVariable Long spotId) {
        return ResponseEntity.ok(voteService.updateCourseByVote(voteId, spotId));
    }

}
