package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.awshandler.CustomException;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.awshandler.FileSizeErrorException;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.awshandler.FileTypeErrorException;
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
import java.io.*;
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
    public String bucket;  // S3 ?????? ??????

    public ResponseDto<?> uploadPost(Long id,
                                     HttpServletRequest request,
                                     MultipartFile multipartFile,
                                     String aStatic) throws IOException {

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
        }

        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("?????????????????? ????????????.")
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

    // ????????? ?????? ????????? ??????
    private Optional<File> convert(MultipartFile file) throws IOException {

        String type = file.getContentType();
        long size = file.getSize();
        System.out.println("===================================="+type);
        System.out.println("===================================="+size);

        // ?????? ?????? ????????????
        if (!type.startsWith("image")) {

            throw new FileTypeErrorException();

        }
        // ?????? ?????? ????????????
        if (size >=20480){

            throw new FileSizeErrorException();
        }

        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        //File convertFile = new File(System.getProperty("image") + "/" + file.getOriginalFilename());

        if (convertFile.createNewFile()) { // ?????? ????????? ????????? ????????? File??? ????????? (????????? ?????????????????? ?????? ?????????)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream ???????????? ????????? ????????? ??????????????? ???????????? ??????
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    // ????????? ????????? ????????? S3??? ??????????????? ?????? ??? S3??? ?????????
    public String upload(File uploadFile, String dirName) {
        // S3??? ????????? ?????? ??????
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        // s3??? ????????? ???  ????????? ????????? url??? String?????? ??????.
        String uploadImageUrl = putS3(uploadFile, fileName);
        log.info(uploadImageUrl);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }
    // ????????? ????????????.*************************
    // ????????? ?????? ????????????
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)  // ?????? ????????? ??? ????????? ??????
                .orElseThrow(() -> new IllegalArgumentException("error: ?????? ????????? ??????????????????"));

        return upload(uploadFile, dirName);
    }

    // S3??? ????????? ?????? ?????????
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // s3??? ?????? ????????? ????????? ????????? ????????? ????????? ?????????
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }


    // ?????? ?????? ????????????


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
//            System.out.println("????????? ???????????????.");
//        }
//    }

}
