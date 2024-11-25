package org.example.flowday.domain.member.exception;

import org.springframework.http.HttpStatus;

public enum MemberException {
    MEMBER_NOT_FOUND("존재하지 않는 ID입니다.", HttpStatus.NOT_FOUND),
    MEMBER_IMAGE_NOT_MODIFIED("이미지 변경 실패", HttpStatus.BAD_REQUEST),
    MEMBER_IMAGE_NOT_SAVED("이미지 저장 실패", HttpStatus.BAD_REQUEST),
    MEMBER_NAME_NOT_FOUND("해당 이름으로 회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    MEMBER_EMAIL_NOT_FOUND("해당 이메일로 회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

    MemberException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public MemberTaskException getMemberTaskException() {
        return new MemberTaskException(this.message,this.status.value());
    }
}
