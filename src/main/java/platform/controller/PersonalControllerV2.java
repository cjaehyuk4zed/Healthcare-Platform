package platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import platform.dto.PersonalDTO;
import platform.dto.userdto.UserInfoDTO;
import platform.dto.userdto.UserTabDTO;
import platform.service.AuthService;
import platform.service.PostingInfoService;
import platform.service.URIService;
import platform.service.UserInfoService;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v2/personals")
@Tag(name = "PersonalV2", description = "개인 맞춤형 시스템 관련 API")
public class PersonalControllerV2 {

    private final PostingInfoService postingInfoService;
    private final UserInfoService userInfoService;
    private final URIService uriService;
    private final AuthService authService;

    // 사용자 배너와 프사와 개인정보 수정까지 전부 한꺼번에?
    // 아니면 부분부분으로 구역을 나누어서?
//    @Operation(summary = "마이 페이지의 정보 및 배너 사진 업로드", description = "마이페이지 정보 및 배너 사진 업로드")
//    @PostMapping(value = "/main-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> saveMainInfo(@RequestPart(name = "banner")MultipartFile multipartFile,
//                                               @RequestPart(name = "profile-pic") ){
//
//        return ResponseEntity.ok().body("User page saved successfully.");
//    }

    // 사용자 페이지 상단 정보
    @Operation(summary = "사용자 페이지 상단 정보", description = "사용자 프사, QR 코드 이미지와 함께 userId, 이름 등 정보")
    @GetMapping("/{userId}/top-info")
    public ResponseEntity<PersonalDTO.TopInfo> getPersonalTopInfo(@PathVariable(name = "userId") String userId) throws FileNotFoundException, BadRequestException {

        boolean isPrincipal = authService.isPrincipal();
        UserInfoDTO.Response userInfoDTO = userInfoService.findUserProfile(userId);

        PersonalDTO.TopInfo topInfo = PersonalDTO.TopInfo.builder()
                .profilePic(uriService.setUserProfilePicURI(userId))
                .qrCode(uriService.setUserQrCodeURI(userId))
                .userId(userId)
                .firstName(userInfoDTO.getFirstName())
                .lastName(userInfoDTO.getLastName())
                .email(userInfoDTO.getEmail())
                .phone(userInfoDTO.getPhone())
                .isPrincipal(isPrincipal)
                .build();

        return ResponseEntity.ok(topInfo);
    }

    // 사용자 페이지 사이드 정보
    @Operation(summary = "사용자 페이지 좌측 사이드 정보", description = "사용자 가입일자, 마지막 활동 시간, 팔로워 수 등 정보")
    @GetMapping("/{userId}/left-info")
    public ResponseEntity<PersonalDTO.LeftInfo> getPersonalLeftInfo(@PathVariable(name = "userId") String userId) throws BadRequestException {

        boolean isPrincipal = authService.isPrincipal();
        UserInfoDTO.Response userInfoDTO = userInfoService.findUserProfile(userId);

        PersonalDTO.LeftInfo leftInfo = PersonalDTO.LeftInfo.builder()
                .dateRegistered(userInfoDTO.getDateRegistered())
                .timestamp(userInfoDTO.getTimestamp())
                .postingCount(userInfoDTO.getPostCount())
                .followingCount(userInfoDTO.getFollowingCount())
                .followerCount(userInfoDTO.getFollowerCount())
                .build();

        return ResponseEntity.ok(leftInfo);
    }
    


    // 사용자 페이지 우측 정보
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserTabDTO.Header.class)))
    @Operation(summary = "사용자 메인 페이지의 탭 목록 불러오기", description = "userId로 해당 사용자의 모든 탭 호출. 각 탭의 이름과 tabId를 사용자 지정 순서대로 나열된 목록으로 가져온다.")
    @GetMapping(value = "/{userId}/tab-info/headers")
    public ResponseEntity<List<UserTabDTO.Header>> getTabHeaders(@PathVariable(name = "userId") String userId){

        List<UserTabDTO.Header> tabHeaders = userInfoService.getUserTabHeaders(userId);

        return ResponseEntity.ok().body(tabHeaders);
    }

    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserTabDTO.Response.class)))
    @Operation(summary = "선택된 탭의 정보 가져오기", description = "userId와 tabId로 탭을 호출. 해당 탭의 모든 정보를 가져온다.")
    @GetMapping(value = "/{userId}/tab-info/{tabId}")
    public ResponseEntity<UserTabDTO.Response> getUserTab(@PathVariable(name = "userId") String userId,
                                                          @PathVariable(name = "tabId") String tabId) throws BadRequestException {
        UserTabDTO.Response userTab = userInfoService.getUserTab(userId, tabId);

        return ResponseEntity.ok().body(userTab);
    }

}
