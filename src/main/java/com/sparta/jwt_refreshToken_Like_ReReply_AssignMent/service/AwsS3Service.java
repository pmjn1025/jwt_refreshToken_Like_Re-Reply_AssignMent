package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.awshandler.CustomException;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.PostResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.ResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Images;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Member;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.domain.Post;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.jwt.TokenProvider;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.ImagesRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.repository.PostRepository;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.awshandler.ErrorCode.IMAGE_NOT_CONVERT;

@RequiredArgsConstructor
@Slf4j
@Service
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;

    private final ImagesRepository imagesRepository;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    public ResponseDto<?> uploadPost(Long id,
                                     HttpServletRequest request,
                                     MultipartFile multipartFile,
                                     String aStatic) throws IOException {

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당아이디가 없습니다.")
        );

        String imageUrl = upload(multipartFile, aStatic);

        Images images = new Images(imageUrl, member.getId(),post.getId());

        imagesRepository.save(images);

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likes(post.getLikes_count())
                        .commentCount(post.getComment_count())
                        .imgUrl(imageUrl)
                        //.commentResponseDtoList(commentResponseDtoList)
                        .author(post.getMember().getNickname())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    // 로컬에 저장된 파일을 S3로 파일업로드 세팅 및 S3로 업로드
    public String upload(File uploadFile, String dirName) {
        // S3에 저장될 파일 이름
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        // s3로 업로드 및  업로드 파일의 url을 String으로 받음.
        String uploadImageUrl = putS3(uploadFile, fileName);
        log.info(uploadImageUrl);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }
    // 이부분 질문하기.*************************
    // 이미지 변환 예외처리
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: 파일 변환에 실패했습니다"));

        return upload(uploadFile, dirName);
    }

    // S3로 업로드 하는 메서드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // s3에 파일 업로드 성공시 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }


    // 파일 변환 예외처리


//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//
//    public String uploadFileV1(MultipartFile multipartFile) {
//
//        validateFileExists(multipartFile);
//
//        String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename());
//
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType(multipartFile.getContentType());
//
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        } catch (IOException e) {
//            //throw new FileUploadFailedException();
//        }
//
//        return amazonS3Client.getUrl(bucketName, fileName).toString();
//
//
//    }
//
//
//    private void validateFileExists(MultipartFile multipartFile) {
//        if (multipartFile.isEmpty()) {
//            System.out.println("파일이 비었습니다.");
//        }
//    }

}
