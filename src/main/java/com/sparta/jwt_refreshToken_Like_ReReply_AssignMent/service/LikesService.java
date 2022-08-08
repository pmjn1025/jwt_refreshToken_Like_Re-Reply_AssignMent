package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.*;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.*;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.jwt.TokenProvider;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {


    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final CommentReplyRepository commentReplyRepository;

    private final LikesRepository likesRepository;

    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> addLikesPost(Long id, HttpServletRequest request) {

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

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 입니다.");
        }

        // 좋아요 한번만 체크
        if (likesRepository.existsByMember_IdAndPost_Id(member.getId(), post.getId())) {

            return ResponseDto.fail("NickName_Is_Already exist", "이미 좋아요를 누르셨습니다.");

        }

        Likes likes = Likes.builder()
                .post(post)
                .member(member)
                .build();

        likesRepository.save(likes);

        // 좋아요 컬럼 갯수 받아와서
        Integer postlike_count = likesRepository.countAllByPost(post);
        // 포스트 업데이트
        post.updatelike_count(postlike_count);

        // 해당 포스트 댓글 수 받아와서
        Integer commentlike_count = commentRepository.countAllByPost(post);
        // 포스트 업데이트
        post.updatecomment_count(commentlike_count);

        //업데이트 된것 저장
        postRepository.save(post);

        // 최종 출력
        // LikePostResponseDto를 쓴이유는 댓글, 대댓글 내용이 필요없다.
        return ResponseDto.success(
                LikePostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .author(post.getMember().getNickname())
                        .likes(post.getLikes_count())
                        .commentCount(post.getComment_count())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );

    }

    // 포스트 좋아요 취소
    @Transactional
    public ResponseDto<?> deleteLikesPost(Long id, HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        // 좋아요 누른 아이디와 해당 게시글이 맞으면 해당아이디를 지우자.
        if (likesRepository.existsByMember_IdAndPost_Id(member.getId(), post.getId())) {
            // 아래 코드와 같은 기초를 생각하자.
            // like 테이블 인덱스 값을 검색해서 삭제
            //likesRepository.deleteById(member.getId());
            // like 테이블 멤버아이디 값을 검색해서 삭제
            likesRepository.deleteByMember_IdAndPost_Id(member.getId(), post.getId());


            // 좋아요 컬럼 갯수 받아와서
            Integer postlike_count = likesRepository.countAllByPost(post);
            // 포스트 업데이트
            post.updatelike_count(postlike_count);

            postRepository.save(post);

            // 최종 출력
            // LikePostResponseDto를 쓴이유는 댓글, 대댓글 내용이 필요없다.
            return ResponseDto.success(
                    LikePostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .author(post.getMember().getNickname())
                            .likes(post.getLikes_count())
                            .commentCount(post.getComment_count())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );


        } else {

            return ResponseDto.fail("NickName_Is_NO exist",
                    "해당 게시글에 좋아요를 누르시지 않으셨습니다.");

        }

    }
    // 좋아요 댓글
    @Transactional
    public ResponseDto<?> addLikesComment(Long id, HttpServletRequest request) {

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
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }

        // 좋아요 한번만 체크
        if (likesRepository.existsByMember_IdAndComment_Id(member.getId(), comment.getId())) {

            return ResponseDto.fail("NickName_Is_Already exist", "이미 좋아요를 누르셨습니다.");

        }

        Likes likes = Likes.builder()
                .comment(comment)
                .member(member)
                .build();

        likesRepository.save(likes);

        // 좋아요 컬럼 갯수 받아와서
        Integer postlike_count = likesRepository.countAllByComment(comment);
        // 포스트 업데이트
        comment.updatelike_count(postlike_count);

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
    // 댓글 좋아요 취소
    @Transactional
    public ResponseDto<?> deleteLikesComment(Long id, HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Comment comment = isPresentComment(id);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        if (likesRepository.existsByMember_IdAndComment_Id(member.getId(), comment.getId())) {
            // 아래 코드와 같은 기초를 생각하자.
            // like 테이블 인덱스 값을 검색해서 삭제
            //likesRepository.deleteById(member.getId());
            // like 테이블 멤버아이디 값을 검색해서 삭제
            likesRepository.deleteByMember_IdAndComment_Id(member.getId(), comment.getId());


            // 좋아요 컬럼 갯수 받아와서
            Integer postlike_count = likesRepository.countAllByComment(comment);
            // 포스트 업데이트
            comment.updatelike_count(postlike_count);

            commentRepository.save(comment);

            // 최종 출력

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


        } else {

            return ResponseDto.fail("NickName_Is_NO exist",
                    "해당 게시글에 좋아요를 누르시지 않으셨습니다.");

        }


    }
    // 대댓글 좋아요 추가
    @Transactional
    public ResponseDto<?> addLikesCommentReply(Long id, HttpServletRequest request) {

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
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 입니다.");
        }

        // 좋아요 한번만 체크
        if (likesRepository.existsByMember_IdAndCommentReply_Id(member.getId(), commentReply.getId())) {

            return ResponseDto.fail("NickName_Is_Already exist", "이미 좋아요를 누르셨습니다.");

        }

        Likes likes = Likes.builder()
                .commentReply(commentReply)
                .member(member)
                .build();

        likesRepository.save(likes);

        // 좋아요 컬럼 갯수 받아와서
        Integer postlike_count = likesRepository.countAllByCommentReply(commentReply);
        // 포스트 업데이트
        commentReply.updatelike_count(postlike_count);

        commentReplyRepository.save(commentReply);

        return ResponseDto.success(
                CommentReplyResponseDto.builder()
                        .id(commentReply.getId())
                        .author(commentReply.getMember().getNickname())
                        .content(commentReply.getContent())
                        .likes(commentReply.getLikes_count())
                        .createdAt(commentReply.getCreatedAt())
                        .modifiedAt(commentReply.getModifiedAt())
                        .build()
        );

    }
    // 대댓글 좋아요 삭제
    @Transactional
    public ResponseDto<?> deleteLikesCommentReply(Long id, HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        CommentReply commentReply = isPresentCommentReply(id);
        if (null == commentReply) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        if (likesRepository.existsByMember_IdAndCommentReply_Id(member.getId(), commentReply.getId())) {
            // 아래 코드와 같은 기초를 생각하자.
            // like 테이블 인덱스 값을 검색해서 삭제
            //likesRepository.deleteById(member.getId());
            // like 테이블 멤버아이디 값을 검색해서 삭제
            likesRepository.deleteByMember_IdAndCommentReply_Id(member.getId(), commentReply.getId());

            // 좋아요 컬럼 갯수 받아와서
            Integer postlike_count = likesRepository.countAllByCommentReply(commentReply);
            // 포스트 업데이트
            commentReply.updatelike_count(postlike_count);

            commentReplyRepository.save(commentReply);

            // 최종 출력
            return ResponseDto.success(
                    CommentResponseDto.builder()
                            .id(commentReply.getId())
                            .author(commentReply.getMember().getNickname())
                            .content(commentReply.getContent())
                            .likes(commentReply.getLikes_count())
                            .createdAt(commentReply.getCreatedAt())
                            .modifiedAt(commentReply.getModifiedAt())
                            .build()
            );


        } else {

            return ResponseDto.fail("NickName_Is_NO exist",
                    "해당 게시글에 좋아요를 누르시지 않으셨습니다.");

        }

    }




    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

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
