package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
  private String title;
  private String content;
  private String imgUrl;
}
