package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service;


import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.CommentReplyRequestDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.CommentReplyResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.CommentResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.ResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Comment;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.CommentReply;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Member;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.jwt.TokenProvider;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.CommentReplyRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentReplyService {

    private final CommentReplyRepository commentReplyRepository;
    private final TokenProvider tokenProvider;
    private final PostService postService;
    private final CommentService commentService;

    // 대댓글 생성
    @Transactional
    public ResponseDto<?> createCommentReply(CommentReplyRequestDto requestDto,
                                             HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }


        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        // 대댓글이지만 게시판 번호 불러오기.
        Post post = comment.getPost();


        CommentReply commentReply = CommentReply.builder()
                .member(member)
                .comment(comment)
                .post(post)
                .content(requestDto.getContent())
                .build();

        commentReplyRepository.save(commentReply);

//        List<CommentReply> commentReplyList = new ArrayList<>();
//        commentReplyList.add(commentReply);
//
//        Comment comment1 = new Comment(commentReplyList);

        return ResponseDto.success(
                CommentReplyResponseDto.builder()
                        .id(commentReply.getId())
                        .commentId(comment.getId())
                        .author(commentReply.getMember().getNickname())
                        .content(commentReply.getContent())
                        .likes(0)
                        .createdAt(commentReply.getCreatedAt())
                        .modifiedAt(commentReply.getModifiedAt())
                        .build()
        );


    }
    
    // 대댓글 수정
    @Transactional
    public ResponseDto<?> updateCommentReply(Long id,
                                             CommentReplyRequestDto requestDto,
                                             HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        CommentReply commentReply = isPresentCommentReply(id);

        if (null == commentReply) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
        }

        if (commentReply.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        commentReply.update(requestDto);

        commentReplyRepository.save(commentReply);

        return ResponseDto.success(
                CommentReplyResponseDto.builder()
                        .id(commentReply.getId())
                        .commentId(commentReply.getComment().getId())
                        .author(commentReply.getMember().getNickname())
                        .content(commentReply.getContent())
                        .likes(commentReply.getLikes_count())
                        .createdAt(commentReply.getCreatedAt())
                        .modifiedAt(commentReply.getModifiedAt())
                        .build()
        );

    }
    @Transactional
    public ResponseDto<?> deleteCommentReply(Long id, HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        CommentReply commentReply = isPresentCommentReply(id);
        if (null == commentReply) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (commentReply.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        commentReplyRepository.delete(commentReply);
        return ResponseDto.success("success");

    }



    //===============아래 부터는 메서드=================


    @Transactional(readOnly = true)
    public CommentReply isPresentCommentReply(Long id) {
        Optional<CommentReply> optionalCommentReply = commentReplyRepository.findById(id);
        return optionalCommentReply.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }



}
