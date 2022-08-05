package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository;


import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findById(Long id);
  Optional<Member> findByNickname(String nickname);
}
