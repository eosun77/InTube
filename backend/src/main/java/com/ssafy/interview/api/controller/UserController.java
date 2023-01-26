package com.ssafy.interview.api.controller;


import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.interview.api.request.User.UserModifyPostReq;
import com.ssafy.interview.api.request.User.UserRegisterPostReq;
import com.ssafy.interview.api.response.User.UserRes;
import com.ssafy.interview.api.service.S3Uploader;
import com.ssafy.interview.api.service.UserService;
import com.ssafy.interview.common.auth.SsafyUserDetails;
import com.ssafy.interview.common.model.response.BaseResponseBody;
import com.ssafy.interview.db.entitiy.User;
import com.ssafy.interview.db.repository.UserRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 유저 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "유저 API", tags = {"User"})
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @Autowired
    S3Uploader s3Uploader;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping()
    @ApiOperation(value = "회원 가입", notes = "사용자 정보를 입력 받아 DB에 insert한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 409, message = "이메일 중복"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> register(
            @RequestBody @ApiParam(value = "회원가입 정보", required = true) UserRegisterPostReq registerInfo) {
        logger.info("register call!");

        if (userRepository.findByEmail(registerInfo.getEmail()).isPresent()) {
            // 이미 회원가입한 회원일 때
            return ResponseEntity.status(409).body(BaseResponseBody.of(409, "Duplicated Email"));
        }

        //임의로 리턴된 User 인스턴스. 현재 코드는 회원 가입 성공 여부만 판단하기 때문에 굳이 Insert 된 유저 정보를 응답하지 않음.
        userService.createUser(registerInfo);

        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
    }

    @PutMapping()
    @ApiOperation(value = "회원 정보 수정", notes = "사용자 정보를 입력 받아 DB에 update한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "잘못된 비밀번호"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 403, message = "권한 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> modify(
            @RequestBody @ApiParam(value = "프로필 수정 정보", required = true) UserModifyPostReq modifyInfo,
            @ApiIgnore Authentication authentication) {
        logger.info("modify call!");

        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
        String email = userDetails.getUsername();

        if (!email.equals(modifyInfo.getEmail())) {
            // 토큰에 저장된 email과 요청을 보낸 email이 다를 때
            return ResponseEntity.status(403).body(BaseResponseBody.of(403, "Forbidden"));
        }

        User user = userRepository.findByEmail(email).get();
        if (!passwordEncoder.matches(modifyInfo.getPassword(), user.getPassword())) {
            // 비밀번호가 틀렸을 때
            return ResponseEntity.status(400).body(BaseResponseBody.of(400, "Invalid Password"));
        }

        userService.updateUser(modifyInfo);
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
    }

    @PostMapping("/image")
    @ApiOperation(value = "프로필 이미지 수정", notes = "이미지 파일을 받아 프로필 이미지 수정한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 403, message = "권한 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> uploadImage(
            @RequestPart @ApiParam(value = "프로필 이미지 파일", required = true) MultipartFile image,
            @ApiIgnore Authentication authentication) throws IOException {
        logger.info("uploadImage call!");

        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
        String email = userDetails.getUsername();

        String img = s3Uploader.upload(image, "profile");
        logger.info("url >>> " + img);

        userService.uploadImage(email, img);

        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
    }

//    @DeleteMapping()
//    @ApiOperation(value = "회원 탈퇴", notes = "아이디인 유저의 이메일을 통해 회원탈퇴 한다.")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "성공"),
//            @ApiResponse(code = 401, message = "인증 실패"),
//            @ApiResponse(code = 404, message = "사용자 없음"),
//            @ApiResponse(code = 500, message = "서버 오류")
//    })
//    public ResponseEntity<? extends BaseResponseBody> remove(
//            @RequestBody @ApiParam(value = "탈퇴할 회원의 이메일", required = true) String email) {
//        userService.deleteUser(email);
//        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
//    }

//    @PostMapping("/find")
//    @ApiOperation(value = "회원 찾기", notes = "이메일로 회원을 찾는다")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "성공"),
//            @ApiResponse(code = 401, message = "인증 실패"),
//            @ApiResponse(code = 404, message = "사용자 없음"),
//            @ApiResponse(code = 500, message = "서버 오류")
//    })
//    public ResponseEntity<?> findUserInfo(
//            @RequestBody @ApiParam(value = "찾는 회원의 이메일", required = true) String email) {
//        /**
//         * 요청 헤더 액세스 토큰이 포함된 경우에만 실행되는 인증 처리이후, 리턴되는 인증 정보 객체(authentication) 통해서 요청한 유저 식별.
//         * 액세스 토큰이 없이 요청하는 경우, 403 에러({"error": "Forbidden", "message": "Access Denied"}) 발생.
//         */
//        Optional<User> user = userService.testUserByEmail(email);
//        if (user.isPresent()) {
//            return ResponseEntity.status(200).body(user);
//        } else {
//            return ResponseEntity.status(200).body("찾으시는 이메일에 해당하는 유저가 없습니다. ");
//        }
//    }

    @GetMapping("/me")
    @ApiOperation(value = "회원 본인 정보 조회", notes = "로그인한 회원 본인의 정보를 응답한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<UserRes> getUserInfo(@ApiIgnore Authentication authentication) {
        /**
         * 요청 헤더 액세스 토큰이 포함된 경우에만 실행되는 인증 처리이후, 리턴되는 인증 정보 객체(authentication) 통해서 요청한 유저 식별.
         * 액세스 토큰이 없이 요청하는 경우, 403 에러({"error": "Forbidden", "message": "Access Denied"}) 발생.
         */
        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getDetails();
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.status(200).body(UserRes.of(user));
    }

    @GetMapping("/nickname")
    @ApiOperation(value = "닉네임으로 회원 조회", notes = "닉네임을 입력받아 회원을 조회한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공", response = BaseResponseBody.class),
            @ApiResponse(code = 409, message = "닉네임 줌복"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> confirmNickname(@RequestParam String nickname) {
        logger.info("confirmNickname call!");

        if (userRepository.findByNickname(nickname).isPresent()) {
            // 중복된 닉네임일 때
            return ResponseEntity.status(409).body(BaseResponseBody.of(409, "Duplicated Nickname"));
        }
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
    }
}
