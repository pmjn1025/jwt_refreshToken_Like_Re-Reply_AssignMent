package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.utils;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.LikesRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.PostRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
@Slf4j
@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {

    private final PostRepository postRepository;

    private final LikesRepository likesRepository;

    private final PostService postService;

    // 초, 분, 시, 일, 월, 주 순서
    // 확인용 30초 나중에 1시로 바꾸기
    @Scheduled(cron = "20 * * * * *")
    @Transactional
    public void updatePrice() throws InterruptedException {
        System.out.println("가격 업데이트 실행");
        // 저장된 모든 관심상품을 조회합니다.
        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            // 1초에 한 상품 씩 조회합니다 (Naver 제한)
            //TimeUnit.SECONDS.sleep(1);
            // i 번째 관심 상품을 꺼냅니다.
            Post p = post;
            // i 번째 관심 상품의 제목으로 검색을 실행합니다.
            long comment_count = p.getComment_count();
            long like_count = p.getLikes_count();

            //여기서 부터 오류 잡기 영기님께 여쭤보기.
            if (comment_count == 0) {
                // i 번째 관심 상품 정보를 업데이트합니다.
                postRepository.deleteById(post.getId());
//                likesRepository.deleteByPost(post);

                if(like_count >=1){

                    likesRepository.deleteByPost(post);
                    // 왜 되지???
                    postRepository.deletePostById(post.getId());
                    // 왜안되지??
                    //postRepository.deleteById(post.getId());

                }

            }
        }
    }


}
