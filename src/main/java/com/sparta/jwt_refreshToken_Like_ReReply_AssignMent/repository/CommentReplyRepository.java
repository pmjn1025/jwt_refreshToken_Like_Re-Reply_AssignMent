package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Comment;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.CommentReply;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReplyRepository extends JpaRepository<CommentReply,Long> {

    List<CommentReply> findAllByComment_Id(Long abc);

    List<CommentReply> findAllByPost(Post post);
}
