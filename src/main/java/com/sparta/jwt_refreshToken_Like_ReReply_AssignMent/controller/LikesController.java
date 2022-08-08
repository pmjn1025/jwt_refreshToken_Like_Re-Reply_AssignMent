package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.PostRequestDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.ResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class LikesController {

    private final LikesService likesService;


    //post 좋아요 추가 //@Pathvariable, HttpServletRequest request 받기.
    @RequestMapping(value = "/api/auth/addlikespost/{id}", method = RequestMethod.POST)
    public ResponseDto<?> addLikesPost(@PathVariable Long id,
                                     HttpServletRequest request) {
        return likesService.addLikesPost(id, request);
    }

    //post 좋아요 삭제

    @RequestMapping(value = "/api/auth/deletelikespost/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteLikesPost(@PathVariable Long id,
                                        HttpServletRequest request) {
        return likesService.deleteLikesPost(id, request);
    }

    //comment 좋아요 추가
    @RequestMapping(value = "/api/auth/addlikescomment/{id}", method = RequestMethod.POST)
    public ResponseDto<?> addLikesComment(@PathVariable Long id,
                                       HttpServletRequest request) {
        return likesService.addLikesComment(id, request);
    }

    //comment 좋아요 삭제
    @RequestMapping(value = "/api/auth/deletelikescomment/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteLikesComment(@PathVariable Long id,
                                          HttpServletRequest request) {
        return likesService.deleteLikesComment(id, request);
    }

    //commentreply 좋아요 추가
    @RequestMapping(value = "/api/auth/addlikescommentreply/{id}", method = RequestMethod.POST)
    public ResponseDto<?> addLikesCommentReply(@PathVariable Long id,
                                          HttpServletRequest request) {
        return likesService.addLikesCommentReply(id, request);
    }

    // commentreply 좋아요 삭제
    @RequestMapping(value = "/api/auth/deletelikescommentreply/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteLikesCommentReply(@PathVariable Long id,
                                             HttpServletRequest request) {
        return likesService.deleteLikesCommentReply(id, request);
    }

}
