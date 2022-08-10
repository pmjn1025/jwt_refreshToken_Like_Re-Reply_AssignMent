package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.awshandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
