package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.MypageRequestDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.ResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    //accesstoken으로 마이페이지 접근
    @RequestMapping(value = "/api/auth/mypageaccesstoken", method = RequestMethod.GET)
    public ResponseDto<?> mypageAccessToken(HttpServletRequest request) {
        return mypageService.mypageAccessToken(request);
    }

    // /api/post/
    // accesstoken 없이 마이페이지 접근
    @RequestMapping(value = "/api/post/mypagenotaccesstoken", method = RequestMethod.GET)
    public ResponseDto<?> mypagenotaccesstoken(@RequestBody MypageRequestDto mypageRequestDto) {
        return mypageService.mypagenotaccesstoken(mypageRequestDto);
    }


}
