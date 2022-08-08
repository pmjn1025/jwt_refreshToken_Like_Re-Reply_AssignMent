package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.MypageRequestDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.*;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Comment;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.CommentReply;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Member;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.jwt.TokenProvider;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final CommentReplyRepository commentReplyRepository;

    private final LikesRepository likesRepository;

    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> mypageAccessToken(HttpServletRequest request) {

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

        List<Post> postList = postRepository.findAllByMember_Id(member.getId());
        List<PostResponseDto> postResponseDtoLike = new ArrayList<>();
        List<PostResponseDto> postResponseDtoNotLike = new ArrayList<>();
        for (Post post : postList){

            if(post.getLikes_count().equals(0)){

                postResponseDtoNotLike.add(
                        PostResponseDto.builder()
                                .id(post.getId())
                                .author(post.getMember().getNickname())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .likes(post.getLikes_count())
                                .commentCount(post.getComment_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(post.getCreatedAt())
                                .modifiedAt(post.getModifiedAt())
                                .build()
                );

            }else {

                postResponseDtoLike.add(
                        PostResponseDto.builder()
                                .id(post.getId())
                                .author(post.getMember().getNickname())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .likes(post.getLikes_count())
                                .commentCount(post.getComment_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(post.getCreatedAt())
                                .modifiedAt(post.getModifiedAt())
                                .build()
                );
            }
        }

        List<Comment> commentList = commentRepository.findAllByMember_Id(member.getId());
        List<CommentResponseDto> commentResponseDtoLike = new ArrayList<>();
        List<CommentResponseDto> commentResponseDtoNotLike = new ArrayList<>();

        for (Comment comment : commentList){

            if(comment.getLikes_count().equals(0)){

                commentResponseDtoNotLike.add(
                        CommentResponseDto.builder()
                                .id(comment.getId())
                                .author(comment.getMember().getNickname())
                                .content(comment.getContent())
                                .likes(comment.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.

                                .createdAt(comment.getCreatedAt())
                                .modifiedAt(comment.getModifiedAt())
                                .build()
                );

            }else {

                commentResponseDtoLike.add(
                        CommentResponseDto.builder()
                                .id(comment.getId())
                                .author(comment.getMember().getNickname())
                                .content(comment.getContent())
                                .likes(comment.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(comment.getCreatedAt())
                                .modifiedAt(comment.getModifiedAt())
                                .build()
                );
            }
        }

        List<CommentReply> commentReplyList = commentReplyRepository.findAllByMember_Id(member.getId());
        List<CommentReplyResponseDto> commentReplyResponseDtoLike = new ArrayList<>();
        List<CommentReplyResponseDto> commentReplyResponseDtoNotLike = new ArrayList<>();

        for (CommentReply commentReply : commentReplyList){

            if(commentReply.getLikes_count().equals(0)){

                commentReplyResponseDtoNotLike.add(
                        CommentReplyResponseDto.builder()
                                .id(commentReply.getId())
                                .commentId(commentReply.getComment().getId())
                                .author(commentReply.getMember().getNickname())
                                .content(commentReply.getContent())
                                .likes(commentReply.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.

                                .createdAt(commentReply.getCreatedAt())
                                .modifiedAt(commentReply.getModifiedAt())
                                .build()
                );

            }else {

                commentReplyResponseDtoLike.add(
                        CommentReplyResponseDto.builder()
                                .id(commentReply.getId())
                                .commentId(commentReply.getComment().getId())
                                .author(commentReply.getMember().getNickname())
                                .content(commentReply.getContent())
                                .likes(commentReply.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(commentReply.getCreatedAt())
                                .modifiedAt(commentReply.getModifiedAt())
                                .build()
                );
            }
        }


        return ResponseDto.success(
                MypageResponseDto.builder()
                        .postResponseDtoNotLike(postResponseDtoNotLike)
                        .commentResponseDtoNotLike(commentResponseDtoNotLike)
                        .commentReplyResponseDtoNotLike(commentReplyResponseDtoNotLike)
                        .postResponseDtoLike(postResponseDtoLike)
                        .commentResponseDtoLike(commentResponseDtoLike)
                        .commentReplyResponseDtoLike(commentReplyResponseDtoLike)
                        .build()
        );
    }


    @Transactional
    public ResponseDto<?> mypagenotaccesstoken(MypageRequestDto mypageRequestDto) {

        String nickname = mypageRequestDto.getNickname();

        if (!memberRepository.existsByNickname(nickname)) {

            return ResponseDto.fail("NickName_Is_No exist", "닉네임을 찾을 수 없습니다.");

        }else if (nickname.equals("")){

            return ResponseDto.fail("Input nickname", "닉네임을 입력해 주세요.");

        }
        Optional<Member> member = memberRepository.findByNickname(nickname);
        long member_id = member.get().getId();

        List<Post> postList = postRepository.findAllByMember_Id(member_id);
        List<PostResponseDto> postResponseDtoLike = new ArrayList<>();
        List<PostResponseDto> postResponseDtoNotLike = new ArrayList<>();
        for (Post post : postList){

            if(post.getLikes_count().equals(0)){

                postResponseDtoNotLike.add(
                        PostResponseDto.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .author(post.getMember().getNickname())
                                .content(post.getContent())
                                .likes(post.getLikes_count())
                                .commentCount(post.getComment_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(post.getCreatedAt())
                                .modifiedAt(post.getModifiedAt())
                                .build()
                );

            }else {

                postResponseDtoLike.add(
                        PostResponseDto.builder()
                                .id(post.getId())
                                .author(post.getMember().getNickname())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .likes(post.getLikes_count())
                                .commentCount(post.getComment_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(post.getCreatedAt())
                                .modifiedAt(post.getModifiedAt())
                                .build()
                );
            }
        }

        List<Comment> commentList = commentRepository.findAllByMember_Id(member_id);
        List<CommentResponseDto> commentResponseDtoLike = new ArrayList<>();
        List<CommentResponseDto> commentResponseDtoNotLike = new ArrayList<>();

        for (Comment comment : commentList){

            if(comment.getLikes_count().equals(0)){

                commentResponseDtoNotLike.add(
                        CommentResponseDto.builder()
                                .id(comment.getId())
                                .author(comment.getMember().getNickname())
                                .content(comment.getContent())
                                .likes(comment.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.

                                .createdAt(comment.getCreatedAt())
                                .modifiedAt(comment.getModifiedAt())
                                .build()
                );

            }else {

                commentResponseDtoLike.add(
                        CommentResponseDto.builder()
                                .id(comment.getId())
                                .author(comment.getMember().getNickname())
                                .content(comment.getContent())
                                .likes(comment.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(comment.getCreatedAt())
                                .modifiedAt(comment.getModifiedAt())
                                .build()
                );
            }
        }

        List<CommentReply> commentReplyList = commentReplyRepository.findAllByMember_Id(member_id);
        List<CommentReplyResponseDto> commentReplyResponseDtoLike = new ArrayList<>();
        List<CommentReplyResponseDto> commentReplyResponseDtoNotLike = new ArrayList<>();

        for (CommentReply commentReply : commentReplyList){

            if(commentReply.getLikes_count().equals(0)){

                commentReplyResponseDtoNotLike.add(
                        CommentReplyResponseDto.builder()
                                .id(commentReply.getId())
                                .commentId(commentReply.getComment().getId())
                                .author(commentReply.getMember().getNickname())
                                .content(commentReply.getContent())
                                .likes(commentReply.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.

                                .createdAt(commentReply.getCreatedAt())
                                .modifiedAt(commentReply.getModifiedAt())
                                .build()
                );

            }else {

                commentReplyResponseDtoLike.add(
                        CommentReplyResponseDto.builder()
                                .id(commentReply.getId())
                                .commentId(commentReply.getComment().getId())
                                .author(commentReply.getMember().getNickname())
                                .content(commentReply.getContent())
                                .likes(commentReply.getLikes_count())
                                //.commentResponseDtoList(commentReplyResponseDtoList)
                                //여기서  좋아요 갯수를 가져와야 된다.
                                // 예시 .id(commentReplyRepository.countAllByComment(commentReply.getId()))
                                .createdAt(commentReply.getCreatedAt())
                                .modifiedAt(commentReply.getModifiedAt())
                                .build()
                );
            }
        }


        return ResponseDto.success(
                MypageResponseDto.builder()
                        .postResponseDtoNotLike(postResponseDtoNotLike)
                        .commentResponseDtoNotLike(commentResponseDtoNotLike)
                        .commentReplyResponseDtoNotLike(commentReplyResponseDtoNotLike)
                        .postResponseDtoLike(postResponseDtoLike)
                        .commentResponseDtoLike(commentResponseDtoLike)
                        .commentReplyResponseDtoLike(commentReplyResponseDtoLike)
                        .build()
        );

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
