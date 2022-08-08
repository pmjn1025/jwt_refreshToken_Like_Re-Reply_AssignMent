package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository;



import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Comment;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);
  List<Comment> findAllByMember_Id(Long member_Id);

    Comment findByPost(Post post);

  Integer countAllByPost(Post post);


    //List<Comment> findByPost_Id(Long post_id);
}
