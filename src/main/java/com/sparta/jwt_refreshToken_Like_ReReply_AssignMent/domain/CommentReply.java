package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.CommentReplyRequestDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CommentReply extends Timestamped{

    // 기본키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래키
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // 외래키
    // 디폴트로 pk값을 가져온다.
    @JoinColumn(name = "post_id", nullable = false)
    // fetch = FetchType.LAZY 지연참조
    // 다른내용을 항상 참조할 필요는 없다.
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "comment_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @Column(nullable = false)
    private String content;


    public void update(CommentReplyRequestDto commentReplyRequestDto) {
        this.content = commentReplyRequestDto.getContent();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }


}
