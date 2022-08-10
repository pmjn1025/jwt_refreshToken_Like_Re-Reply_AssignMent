package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert // 디폴트가 null일때 나머지만 insert
@Entity
public class Likes {

    // 기본키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래키
    @JoinColumn(name = "member_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // 외래키
    // 디폴트로 pk값을 가져온다.
    @JoinColumn(name = "post_id")
    // fetch = FetchType.LAZY 지연참조
    // 다른내용을 항상 참조할 필요는 없다.
    //@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ManyToOne(fetch = FetchType.LAZY)
    @ColumnDefault("0") //default 0
    private Post post;

    @JoinColumn(name = "comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ColumnDefault("0") //default 0
    private Comment comment;

    @JoinColumn(name = "commentreply_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ColumnDefault("0") //default 0
    private CommentReply commentReply;

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }


}
