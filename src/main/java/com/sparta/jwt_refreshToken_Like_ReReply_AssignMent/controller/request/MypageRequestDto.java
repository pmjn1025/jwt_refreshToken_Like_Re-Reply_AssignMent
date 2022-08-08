package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MypageRequestDto {

    @NotBlank
    private String nickname;

}
