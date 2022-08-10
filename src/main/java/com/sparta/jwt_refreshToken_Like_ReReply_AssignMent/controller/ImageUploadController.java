package com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller;

import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.controller.response.ResponseDto;
import com.sparta.jwt_refreshToken_Like_ReReply_AssignMent.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ImageUploadController {

    private final AwsS3Service awsS3Service;

//    @PostMapping("/images")
//    public String uploadFile(
//            @RequestPart(value = "file") MultipartFile multipartFile) {
//        return awsS3Service.uploadFileV1(multipartFile);
//    }

//    @PostMapping("/images")
//    public String upload(@RequestParam("images") MultipartFile multipartFile) throws IOException, IOException, IOException {
//        return awsS3Service.upload(multipartFile, "static");
//
//    }

    @PostMapping("/images/{id}")
    public ResponseDto<?> uploadPost(@PathVariable Long id,
                                 HttpServletRequest request,
                                 @RequestParam("images") MultipartFile multipartFile) throws IOException, IOException, IOException {
        return awsS3Service.uploadPost(id,request,multipartFile, "static");

    }


}
