package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository;


import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
}
