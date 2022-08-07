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
    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
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
    List<CommentReply> commentReplyList = commentReplyRepository.findAllByPost(post);
    //List<CommentReply> commentReplyList;

    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
    List<CommentReplyResponseDto> commentReplyResponseDtoList = new ArrayList<>();
  // 코멘트 아이디 대댓글아이디가 같으면 연결하라
    for (Comment comment : commentList) {

      //commentReplyList = commentReplyRepository.findAllByComment_Id(comment.getId());

      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build()
      );


      }


    for(CommentReply commentReply : commentReplyList) {

      commentReplyResponseDtoList.add(
              CommentReplyResponseDto.builder()
                      // 코멘트 index
                      .commentId(commentReply.getComment().getId())
                      // 원래 대댓글 index
                      .id(commentReply.getId())
                      .author(commentReply.getMember().getNickname())
                      .content(commentReply.getContent())
                      .createdAt(commentReply.getCreatedAt())
                      .modifiedAt(commentReply.getModifiedAt())
                      .build()
      );
    }


    //return ResponseDto.success(commentResponseDtoList);

//    for(CommentReply commentReply : commentReplyList){
//      commentReplyResponseDtoList.add(
//              CommentReplyResponseDto.builder()
//                      .id(commentReply.getId())
//                      .author(commentReply.getMember().getNickname())
//                      .content(commentReply.getContent())
//                      .createdAt(commentReply.getCreatedAt())
//                      .modifiedAt(commentReply.getModifiedAt())
//                      .build()
//      );
//    }



   // commentResponseDtoList.add((CommentResponseDto) commentReplyList);




    return ResponseDto.success(

                    CommentResponseDto1.builder()
//                    .id(post.getId())
//                    .author(post.getMember().getNickname())
//                    .content(post.getContent())
                    .commentResponseDtoList(commentResponseDtoList)
                    .commentReplyResponseDtoList(commentReplyResponseDtoList)
//                    .author(post.getMember().getNickname())
//                    .createdAt(post.getCreatedAt())
//                    .modifiedAt(post.getModifiedAt())
                    .build()
    );


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
