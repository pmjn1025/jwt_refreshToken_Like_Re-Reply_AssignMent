package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto1 {

    private List<CommentResponseDto> commentResponseDtoList;
    private List<CommentReplyResponseDto> commentReplyResponseDtoList;

}
