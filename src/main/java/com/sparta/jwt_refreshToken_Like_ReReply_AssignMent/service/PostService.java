package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.PostRequestDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.CommentReplyResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.CommentResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.PostResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.ResponseDto;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Comment;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.CommentReply;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Member;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.jwt.TokenProvider;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.CommentReplyRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.CommentRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.LikesRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final CommentReplyRepository commentReplyRepository;

    private final LikesRepository likesRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ???????????????.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ???????????????.");
        }
        // ????????? ????????? ????????? ????????? ?????? ?????????
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
        }

        if (requestDto.getImgUrl().equals("")) {

            Post post = Post.builder()
                    .title(requestDto.getTitle())
                    .content(requestDto.getContent())
                    .imgUrl("0")
                    .likes_count(0)
                    .comment_count(0)
                    .member(member)
                    .build();

            postRepository.save(post);

            return ResponseDto.success(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .imgUrl(post.getImgUrl())
                            .author(post.getMember().getNickname())
                            .likes(post.getLikes_count())
                            .commentCount(post.getComment_count())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );

        } else {

            Post post = Post.builder()
                    .title(requestDto.getTitle())
                    .content(requestDto.getContent())
                    .imgUrl(requestDto.getImgUrl())
                    .likes_count(0)
                    .comment_count(0)
                    .member(member)
                    .build();

            postRepository.save(post);

            return ResponseDto.success(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .imgUrl(post.getImgUrl())
                            .author(post.getMember().getNickname())
                            .likes(post.getLikes_count())
                            .commentCount(post.getComment_count())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
        }

    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long id) {
        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ????????? id ?????????.");
        }

        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {

            List<CommentReply> commentReplyList = commentReplyRepository.findAllByComment(comment);
            List<CommentReplyResponseDto> commentReplyResponseDtoList = new ArrayList<>();

            for (CommentReply commentReply : commentReplyList) {

                commentReplyResponseDtoList.add(
                        CommentReplyResponseDto.builder()
                                // ????????? index
                                .commentId(commentReply.getComment().getId())
                                // ?????? ????????? index
                                .id(commentReply.getId())
                                .author(commentReply.getMember().getNickname())
                                .content(commentReply.getContent())
                                .likes(commentReply.getLikes_count())
                                .createdAt(commentReply.getCreatedAt())
                                .modifiedAt(commentReply.getModifiedAt())
                                .build()
                );
            }


            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .author(comment.getMember().getNickname())
                            .content(comment.getContent())
                            .likes(comment.getLikes_count())
                            .commentReplyResponseDtoList(commentReplyResponseDtoList)
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imgUrl(post.getImgUrl())
                        .likes(post.getLikes_count())
                        .commentCount(post.getComment_count())
                        .commentResponseDtoList(commentResponseDtoList)
                        .author(post.getMember().getNickname())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        return ResponseDto.success(postRepository.findAllByOrderByModifiedAtDesc());
    }

    @Transactional
    public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ???????????????.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ???????????????.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
        }

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ????????? id ?????????.");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "???????????? ????????? ??? ????????????.");
        }

        post.update(requestDto);
        postRepository.save(post);
        return ResponseDto.success(post);
    }

    @Transactional
    public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ???????????????.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ???????????????.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
        }

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "???????????? ?????? ????????? id ?????????.");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "???????????? ????????? ??? ????????????.");
        }

        postRepository.delete(post);
        return ResponseDto.success("delete success");
    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

}
