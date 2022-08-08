package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service;


import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.CommentRequestDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.CommentReplyResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.CommentResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.CommentResponseDto1;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.ResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Comment;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.CommentReply;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Member;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.jwt.TokenProvider;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.CommentReplyRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.CommentRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentReplyRepository commentReplyRepository;

    private final PostRepository postRepository;

    private final TokenProvider tokenProvider;
    private final PostService postService;

    @Transactional
    public ResponseDto<?> createComment(CommentRequestDto requestDto,
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

        Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content(requestDto.getContent())
                .build();
        commentRepository.save(comment);

        // 해당 포스트 댓글 수 받아와서
        Integer commentlike_count = commentRepository.countAllByPost(post);
        // 포스트 업데이트
        post.updatecomment_count(commentlike_count);
        //업데이트 된것 저장
        postRepository.save(post);

        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .author(comment.getMember().getNickname())
                        .content(comment.getContent())
                        .likes(0)
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCommentsByPost(Long postId) {
        Post post = postService.isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }
        List<Comment> commentList = commentRepository.findAllByPost(post);
        //List<CommentReply> commentReplyList = commentReplyRepository.findAllByPost(post);
        //List<CommentReply> commentReplyList;

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        //List<CommentReplyResponseDto> commentReplyResponseDtoList = new ArrayList<>();
        // 코멘트 아이디 대댓글아이디가 같으면 연결하라
        for (Comment comment : commentList) {

            List<CommentReply> commentReplyList = commentReplyRepository.findAllByComment(comment);
            List<CommentReplyResponseDto> commentReplyResponseDtoList = new ArrayList<>();

            for (CommentReply commentReply : commentReplyList) {

                commentReplyResponseDtoList.add(
                        CommentReplyResponseDto.builder()
                                // 코멘트 index
                                .commentId(commentReply.getComment().getId())
                                // 원래 대댓글 index
                                .id(commentReply.getId())
                                .author(commentReply.getMember().getNickname())
                                .content(commentReply.getContent())
                                .likes(commentReply.getLikes_count())
                                .createdAt(commentReply.getCreatedAt())
                                .modifiedAt(commentReply.getModifiedAt())
                                .build()
                );
            }

            // commentReplyList = commentReplyRepository.findAllByComment_Id(comment.getId());

            // 좋아요레포지터리.countAllbycommentid(comment.getId())
            // CommentResponseDto에서 long이나 int로 joayo를 선언한다.
            // 테이블에도 좋아요를 만든다?

            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .author(comment.getMember().getNickname())
                            .content(comment.getContent())
                            .likes(comment.getLikes_count())
                            .commentReplyResponseDtoList(commentReplyResponseDtoList)
                            //여기서  좋아요 갯수를 가져와야 된다.
                            // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(commentResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> updateComment(Long id,
                                        CommentRequestDto requestDto,
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

        long someid = member.getId();

        Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Comment comment = isPresentComment(id);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        comment.update(requestDto);

        commentRepository.save(comment);

        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .author(comment.getMember().getNickname())
                        .content(comment.getContent())
                        .likes(comment.getLikes_count())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
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

        Comment comment = isPresentComment(id);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        commentRepository.delete(comment);
        return ResponseDto.success("success");
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
