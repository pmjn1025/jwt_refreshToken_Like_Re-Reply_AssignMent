package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Images extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @Column(name = "imgUrl")
    String imgUrl;

    @Column(name = "member_id")
    Long member_id;

    @Column(name = "post_id")
    Long post_id;

    public Images(String imgUrl, Long member_id, Long post_id){

        this.imgUrl = imgUrl;
        this.member_id = member_id;
        this.post_id = post_id;


    }

}
