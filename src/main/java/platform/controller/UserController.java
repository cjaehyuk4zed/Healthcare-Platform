package platform.controller;

import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import platform.constants.DirectoryMapConstants;
import platform.dto.userdto.UserInfoDTO;
import platform.dto.userdto.UserProfilePicDTO;
import platform.service.FileService;
import platform.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "Users", description = "유저 기능 API")
@Hidden
public class UserController {

    private final FileService fileService;
    private final UserInfoService userInfoService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDTO.class))
            }),
            @ApiResponse(responseCode = "500", description = "Remove userPic and qrCode fields from request parameters")
    })
    @Operation(summary = "사용자 정보 저장", description = "사용자 정보 저장, 프로필 사진은 제외 (전용 API가 따로 있음), 그리고 QR코드는 자동 생성한다")
    @PostMapping("/save") // Save user input to DB, and return UserInfoDTO as JSON data to the frontend
    public ResponseEntity<UserInfoDTO> saveUserData(@RequestBody UserInfoDTO userInfoDTO) throws IOException, WriterException {
        userInfoService.saveUser(userInfoDTO);
        userInfoService.saveQrCode(userInfoDTO.getUserId());
        return new ResponseEntity<>(userInfoDTO, HttpStatus.OK);
    }

    // ADD A FILTER SO THAT ONLY IMAGE FILES ARE UPLOADED
    // ADD A FILTER SO THAT ONLY IMAGE FILES ARE UPLOADED
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile picture saved", content = {
                    @Content(mediaType = "text/plain")
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Please try again")
    })
    @Operation(summary = "POST /api/v2/user-controller/users/{userId} 로 이동", description = "사용자 프로필 사진 변경 및 저장 API")
    @PutMapping(value = "/save/profile-pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfilePicDTO> saveUserPic(
            @RequestPart(required = true) MultipartFile multipartFile,
            @Schema(example = "testuser") @RequestPart(required = true) String userId,
            @Schema(example = "imageId") @RequestPart String imageId) throws IOException {

        Path filePath = Path.of(DirectoryMapConstants.HOME_DIR, userId);
        String fileExtension = multipartFile.getContentType().split("/")[1];
        String fileName = userId + "_pic." + fileExtension;
        userInfoService.saveUserPic(multipartFile, filePath, fileName);

        String userProfilePicSrc = userInfoService.setUserProfilePicSrc(userId);
        UserProfilePicDTO userProfilePicDTO = new UserProfilePicDTO(userProfilePicSrc, userId);

        return ResponseEntity.ok().body(userProfilePicDTO);
    }

    @Operation(summary = "GET /api/v2/user-controller/users/{userId}/profile 로 이동", description = "사용자 프로필 정보 및 프사와 QR 코드에 대한 img src 링크를 반환한다")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "User information found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDTO.Basic.class))
            }),
            @ApiResponse(responseCode = "400", description = "User not found - Invalid ID supplied", content = {
                    @Content(mediaType = "text/plain")
            })
    })
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserInfoDTO.Basic> getUserProfile(@Schema(example = "testuser") @PathVariable String userId) throws BadRequestException {
        UserInfoDTO.Basic userInfoBasicDTO = userInfoService.findUserProfileBasic(userId);
        return ResponseEntity.ok().body(userInfoBasicDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User QR code .png image sent successfully", content = {
                    @Content(mediaType = "image/png")
            }),
            @ApiResponse(responseCode = "500", description = "User QR code not found - Invalid user ID")
    })
    @Operation(summary = "GET /api/v2/user-controller/users/{userId}/qr-code")
    @GetMapping("/get/qr-code/{userId}")
    public ResponseEntity<Resource> getUserQrCode(@Schema(example = "testuser") @PathVariable String userId) throws FileNotFoundException {
        Resource resource = userInfoService.findQrCode(userId);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile image sent successfully", content = {
                    @Content(mediaType = "image (jpg, png, etc)")
            }),
            @ApiResponse(responseCode = "500", description = "User profile image not found - Invalid user ID")
    })
    @Operation(summary = "GET /api/v2/user-controller/users/{userId}/profile-pic 로 이동", description = "사용자 프사 이미지 검색, 프사가 없을 경우 기본 프사를 반환한다")
    @GetMapping("/get/profile-pic/{userId}")
    public ResponseEntity<Resource> getUserProfilePic(@Schema(example = "testuser") @PathVariable String userId) throws FileNotFoundException {
        Resource resource = userInfoService.findUserPic(userId);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        System.out.println("mediaType = " + mediaType);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }
}