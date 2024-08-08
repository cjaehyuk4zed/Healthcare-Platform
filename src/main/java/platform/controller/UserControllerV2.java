package platform.controller;

import com.google.zxing.WriterException;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;
import platform.constants.DirectoryMapConstants;
import platform.dto.CategoryDTO;
import platform.dto.authdto.AuthenticationRequestDTO;
import platform.dto.authdto.AuthenticationResponseDTO;
import platform.dto.postdto.PostInfoResponseDTO;
import platform.dto.postdto.PostingPreviewDTO;
import platform.dto.userdto.UserInfoDTO;
import platform.dto.userdto.UserProfilePicDTO;
import platform.service.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static platform.constants.DirectoryMapConstants.HOME_DIR;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v2/users")
@Tag(name = "UsersV2", description = "유저 기능 API")
public class UserControllerV2 {

    private final UserInfoService userInfoService;
    private final PostingInfoService postingInfoService;
    private final FileService fileService;
    private final AuthService authService;


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDTO.class))
            }),
            @ApiResponse(responseCode = "500", description = "Remove userPic and qrCode fields from request parameters")
    })
    @Operation(summary = "사용자 회원가입 후 첫 정보 등록", description = "사용자 정보 등록, 프로필 사진은 제외 (전용 API가 따로 있음), 그리고 QR코드는 자동 생성한다. 회원가입은 AuthController에 있다.")
    @PostMapping("") // Save user input to DB, and return UserInfoDTO as JSON data to the frontend
    public ResponseEntity<UserInfoDTO> saveUserInfo(
            @Schema(implementation = UserInfoDTO.Save.class)
            @RequestBody UserInfoDTO userInfoDTO) throws IOException, WriterException {
        if(!authService.isCurrentUser(userInfoDTO.getUserId()) && !authService.isAdmin()){
            throw new AccessDeniedException("You do not have permission to edit this user's information");
        }
        userInfoService.saveUser(userInfoDTO);
        userInfoService.saveQrCode(userInfoDTO.getUserId());
        return new ResponseEntity<>(userInfoDTO, HttpStatus.OK);
    }

    // 사용자 정보 중 수정 가능한 부분만 받는다. 즉, 가입 일자, 팔로워 수 등을 제외한 부분들.
    @Operation(summary = "특정 사용자의 정보 수정", description = "사용자 정보 중 수정 가능한 부분만 받는다. 즉, 가입 일자, 팔로워 수 등을 제외한 부분들이다.")
    @PatchMapping("")
    public ResponseEntity<String> updateUserInfo(
            @Schema(implementation = UserInfoDTO.Save.class)
            @RequestBody UserInfoDTO.Save userInfoDTO) throws BadRequestException {
        if(!authService.isCurrentUser(userInfoDTO.getUserId()) && !authService.isAdmin()){
            throw new AccessDeniedException("You do not have permission to edit this user's information");
        }
        userInfoService.updateUser(userInfoDTO);
        return ResponseEntity.ok().body("User profile updated successfully!");
    }

    @Operation(summary = "사용자 계정 탈퇴", description = "해당 사용자 계정 탈퇴. 영구적이며, 되돌릴 수 없다.")
    @DeleteMapping("")
    public ResponseEntity<String> deleteUserInfo(HttpServletRequest request,
            @RequestBody AuthenticationRequestDTO authDTO) throws IOException {

//        삭제하기 전에 다시 한번 비밀번호 입력으로 본인 인증
        AuthenticationResponseDTO responseDTO = authService.authenticate(request, authDTO);
        authService.deleteUser(authDTO.getUserId());

        // 사용자 폴더 삭제
        Path userDir = Path.of(HOME_DIR, authDTO.getUserId());
        fileService.deleteDir(userDir);

        return ResponseEntity.ok().body("Account has been permanently deleted.");
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile picture saved"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Please try again")
    })
    @Operation(summary = "사용자 프로필 사진 저장", description = "사용자 프로필 사진 변경 및 저장 API. 이전 프로필 사진은 삭제하고 덮어씌운다")
    @PostMapping(value = "/profile-pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfilePicDTO> saveUserPic(
            @RequestPart(required = true) MultipartFile multipartFile,
            @Schema(example = "testuser") @RequestPart(required = true) String userId) throws IOException {
        if(!authService.isCurrentUser(userId) && !authService.isAdmin()){throw new AccessDeniedException("You cannot edit this user's profile pic");}
        fileService.isImage(multipartFile);

        Path filePath = Path.of(HOME_DIR, userId);
        String fileExtension = multipartFile.getContentType().split("/")[1];
        String fileName = userId + "_pic." + fileExtension;
        userInfoService.saveUserPic(multipartFile, filePath, fileName);

        log.info("Completed saveUserPic method");

        String userProfilePicSrc = userInfoService.setUserProfilePicSrc(userId);
        UserProfilePicDTO userProfilePicDTO = new UserProfilePicDTO(userProfilePicSrc, userId);

        log.info("UserProfilePicDTO : {}", userProfilePicDTO);

        return ResponseEntity.ok().body(userProfilePicDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile image sent successfully", content = {
                    @Content(mediaType = "image (jpg, png, etc)")
            }),
            @ApiResponse(responseCode = "500", description = "User profile image not found - Invalid user ID")
    })
    @Operation(summary = "사용자 프사 이미지 검색", description = "사용자 프사 이미지 검색, 프사가 없을 경우 기본 프사를 반환한다")
    @GetMapping("/{userId}/profile-pic")
    public ResponseEntity<Resource> getUserProfilePic(@Schema(example = "testuser") @PathVariable String userId) throws FileNotFoundException {
        Resource resource = userInfoService.findUserPic(userId);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        System.out.println("mediaType = " + mediaType);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @Operation(summary = "특정 사용자의 임시 게시글 목록 불러오기", description = "임임시 게시글의 title, timestamp, postId 3가지 값을 목록으로 불러온다. 이 API는 GET요청이지만, 로그인 한 상태에서만 부를 수 있다.")
    @GetMapping("/postings/drafts")
    public ResponseEntity<List<PostInfoResponseDTO.Draft>> getDrafts(@Schema(example = "admin") @RequestParam(name = "userId") String userId){
        if(!authService.isCurrentUser(userId) && !authService.isAdmin()){
            throw new AccessDeniedException("You do not have permission to access this user's drafts");
        }

        List<PostInfoResponseDTO.Draft> drafts = postingInfoService.findAllDraftsByUserId(userId);
        return ResponseEntity.ok().body(drafts);
    }


    /**
    * 사용자 관심 카테고리 (Interests) 관련 API
     * 관심 카테고리 등록, 삭제, 목록보기 등
    * */

    @Operation(summary = "사용자 관심 카테고리 등록", description = "사용자의 관심 카테고리 등록. 추천 게시글 알고리즘에 사용한다.")
    @PostMapping("/interested-category")
    public ResponseEntity<String> addUserInterests(@Schema(implementation = CategoryDTO.class) @RequestBody CategoryDTO categoryDTO){
        String userId = authService.getUserPrincipalOrThrow();
        userInfoService.saveUserInterest(userId, categoryDTO);
        return ResponseEntity.ok("Added category to user interests");
    }

    @Operation(summary = "사용자 관심 카테고리 제거", description = "사용자가 등록한 관심 카테고리 중에서 제거.")
    @DeleteMapping("/interested-categories/{categoryName}")
    public ResponseEntity<String> deleteUserInterests(@RequestBody CategoryDTO categoryDTO) {
        String userId = authService.getUserPrincipalOrThrow();
        userInfoService.deleteUserInterest(userId, categoryDTO);
        return ResponseEntity.ok("Deleted category from user interests");
    }

    @Operation(summary = "사용자 관심 카테고리 목록 불러오기", description = "해당 사용자의 관심 카테고리 전부 불러오기. 상위 카테고리와 하위 카테고리 모두 보여준다.")
    @GetMapping("/{userId}/interested-categories")
    public ResponseEntity<List<CategoryDTO>> getUserInterests(@Schema(example = "admin") @PathVariable(name = "userId") String userId){
        if(!authService.isCurrentUser(userId)){
            throw new AccessDeniedException("You do not have permission to this action.");
        }

        List<CategoryDTO> categoryDTOS = userInfoService.getUserInterests(userId);
        return ResponseEntity.ok().body(categoryDTOS);
    }

    @Operation(summary = "sort 정렬에 따라 사용자 관심 게시글 Top10 목록 반환", description = "recent | likes | views 로 sort 가능하고, sortOrder는 asc(오름차순) 또는 desc(내림차순). 기본은 내림차순.")
    @GetMapping(value = "/postings/top-10")
    public ResponseEntity<List<PostingPreviewDTO>> getRecentPosts(@Schema(example = "recent", description = "정렬 파라미터 : recent | likes | views")
                                                                  @RequestParam(name = "sort") String sort,
                                                                  @Schema(example = "admin", description = "사용자 ID")
                                                                  @RequestParam(name = "userId") String userId){
        int page = 0;
        int size = 10;

        Sort sortBy = Sort.by(Sort.Direction.DESC, sort);
        PageRequest pageable = PageRequest.of(page, size, sortBy);
        log.info("PostController GET sort : {}", sort);

        List<PostingPreviewDTO> postingPreviewDTOS = postingInfoService.getPostsBySortAndInterest(userId, pageable);

        return ResponseEntity.ok().body(postingPreviewDTOS);
    }



    /**
     * 사용자 게시글 좋아요 (likes) 관련 API
     * 좋아요 누르기, 삭제, 목록보기 등
     * */

    @Operation(summary = "지금 조회중인 게시글을 좋아요한 상태인지 확인", description = "좋아요 누른 상태이면 true, 아니라면 false를 반환한다.")
    @GetMapping("/like/{postingId}")
    public ResponseEntity<Boolean> isPostLiked (@Schema(example = "00000000-0000-0000-0000-000000000000")
                                                @PathVariable(name = "postId") String postId){
        String principal = authService.getUserPrincipalOrThrow();
        boolean isLiked = userInfoService.isUserLikedPost(principal, postId);

        return ResponseEntity.ok().body(isLiked);
    }

    @Operation(summary = "게시글 좋아요 버튼", description = "Posting_Info 테이블에서 게시글의 좋아요 수 +1 증가하고, User_Like 테이블에 누가 좋아요 눌렀는지 기록한다")
    @PostMapping("/like")
    public ResponseEntity<String> likePost(@RequestPart(name = "postId") String postId) throws BadRequestException {
        log.info("PUT UserController /{userId}/{postId}/like");
        String principal = authService.getUserPrincipalOrThrow();

        boolean userLiked = userInfoService.userLikesPost(principal, postId);
        if(userLiked){
            // If true, increment total likes +1 in Posting_Info
            postingInfoService.updatePostLikes(postId, true);


            // If true, increment "User_Interest" table likes for this category/subcategory
            PostInfoResponseDTO postInfo = postingInfoService.findByPostingId(postId);
            CategoryDTO categoryDTO = new CategoryDTO(postInfo.getCategory(), postInfo.getSubcategory());
            userInfoService.addUserInterest(principal, categoryDTO);
            return ResponseEntity.ok().body("You liked this post");
        }
        return ResponseEntity.ok().body("You already liked this post");
    }

    @Operation(summary = "게시글 좋아요 취소 버튼", description = "Posting_Info 테이블에서 게시글의 좋아요 수 -1 감소하고, User_Like 테이블에 기록에서 false 값으로 설정 (삭제는 scheduled task 로 일괄 삭제)")
    @DeleteMapping("/like/{postingId}")
    public ResponseEntity<String> likePostRemove(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId) throws BadRequestException {
        log.info("DELETE PostingController /{userId}/{postId}/like");

        String principal = authService.getUserPrincipalOrThrow();

        boolean userLiked = userInfoService.deleteUserLikesPost(principal, postId);
        if(userLiked){
            // If true, subtract total likes -1
            postingInfoService.updatePostLikes(postId, false);

            // If true, subtract "User_Interest" table likes for this category/subcategory
            PostInfoResponseDTO postInfo = postingInfoService.findByPostingId(postId);
            CategoryDTO categoryDTO = new CategoryDTO(postInfo.getCategory(), postInfo.getSubcategory());
            userInfoService.subUserInterest(principal, categoryDTO);
        }
        return ResponseEntity.ok().body("You no longer like this post");
    }

    @Operation(summary = "특정 사용자가 좋아요 누른 게시글 목록 반환")
    @GetMapping("/{userId}/likes")
    public ResponseEntity<List<PostingPreviewDTO>> getUserLikes(@Schema(example = "admin") @PathVariable(name = "userId") String userId){

        List<PostingPreviewDTO> list = userInfoService.getUserLikedPosts(userId);

        return ResponseEntity.ok().body(list);
    }


    /**
     * 사용자 팔로우 좋아요 (followers) 관련 API
     * 팔로잉에 추가, 삭제, 팔로잉 중인 목록보기 등
     * */

    @Operation(summary = "{userId} 사용자를 팔로잉 하기", description = "{userId}가 팔로잉 사용자고, 현재 로그인된 사용자가 -팔로워- 가 된다. User_Info 테이블의 followerCount와 followingCount 도 업데이트 한다.")
    @PostMapping("/follow")
    public void addUserFollower(@RequestPart(name = "userId") String userId) {
        String followerId = authService.getUserPrincipalOrThrow();
        userInfoService.addUserFollower(userId, followerId);
    }

    @Operation(summary = "{userId} 사용자 팔로잉 목록에서 제거하기", description = "{userId}가 팔로잉 중이었던 사용자고, 현재 로그인된 사용자가 -팔로워- 였던 것이다. User_Info 테이블의 followerCount와 followingCount 도 업데이트 한다.")
    @DeleteMapping("/followers/{userId}")
    public void deleteFollowerUser(@PathVariable(name = "userId") String userId){
        String followerId = authService.getUserPrincipalOrThrow();
        userInfoService.deleteUserFollower(userId, followerId);
    }

    @Operation(summary = "지금 조회 중인 사용자를 팔로잉 중인지 확인", description = "팔로잉 중이면 true, 아니라면 false를 반환한다.")
    @GetMapping("/{userId}/following")
    public ResponseEntity<Boolean> isFollowingUser(@Schema(example = "admin", description = "여기서 {userId}는 현재 조회중인 사용자의 userId이다. 현재 로그인한 사용자의 userId가 아님.")
                                                   @PathVariable(name = "userId") String userId){
        if(authService.isCurrentUser(userId)){
            return ResponseEntity.ok().body(false);
        }
        String principal = authService.getUserPrincipalOrThrow();
        boolean isFollower = userInfoService.isUserFollower(userId, principal);

        return ResponseEntity.ok().body(isFollower);
    }

    @Operation(summary = "사용자를 팔로잉 중인 사용자들 목록 가져오기", description = "해당 사용자를 팔로잉 하는 중인 사용자들의 userId 목록을 반환한다.")
    @GetMapping("/{userId}/followers-list")
    public ResponseEntity<List<String>> getUserFollowers(@Schema(example = "admin") @PathVariable(name = "userId") String userId){
        List<String> followersList = userInfoService.getUserFollowers(userId);
        return ResponseEntity.ok().body(followersList);
    }

    @Operation(summary = "사용자가 팔로우 하는 중인 사용자들 목록 가져오기")
    @GetMapping("/{userId}/following-list")
    public ResponseEntity<List<String>> getUserFollowingList(@Schema(example = "admin") @PathVariable(name = "userId") String userId){
        List<String> followingList = userInfoService.getUserFollowingList(userId);
        return ResponseEntity.ok().body(followingList);
    }













    @Operation(summary = "사용자 프로필 정보 조회", description = "사용자 프로필 정보 및 프사와 QR 코드에 대한 img src 링크를 반환한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "User not found - Invalid ID supplied", content = {
                    @Content(mediaType = "text/plain")
            })
    })
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserInfoDTO.Response> getUserProfile(@Schema(example = "admin") @PathVariable String userId) throws BadRequestException {
        UserInfoDTO.Response userInfoDTO = userInfoService.findUserProfile(userId);
        return ResponseEntity.ok().body(userInfoDTO);
    }

    @Operation(summary = "사용자 프로필 정보 조회", description = "사용자 프로필 정보 및 프사와 QR 코드에 대한 img src 링크를 반환한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDTO.Basic.class))
            }),
            @ApiResponse(responseCode = "400", description = "User not found - Invalid ID supplied", content = {
                    @Content(mediaType = "text/plain")
            })
    })
    @GetMapping("/{userId}/profile/basic")
    public ResponseEntity<UserInfoDTO.Basic> getUserProfileBasic(@Schema(example = "admin") @PathVariable String userId) throws BadRequestException {
        UserInfoDTO.Basic userInfoBasicDTO = userInfoService.findUserProfileBasic(userId);
        return ResponseEntity.ok().body(userInfoBasicDTO);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User QR code .png image sent successfully", content = {
                    @Content(mediaType = "image/png")
            }),
            @ApiResponse(responseCode = "500", description = "User QR code not found - Invalid user ID")
    })
    @Operation(summary = "사용자 프로필 QR 코드를 이미지 파일로 받기")
    @GetMapping("/{userId}/qr-code")
    public ResponseEntity<Resource> getUserQrCode(@Schema(example = "testuser") @PathVariable String userId) throws FileNotFoundException {
        Resource resource = userInfoService.findQrCode(userId);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }


    @Operation(summary = "사용자 신고하기", description = "{userId}가 신고 대상 사용자고, 현재 로그인된 사용자가 신고를 하는 사용자다. 비로그인 사용자는 신고를 하지 못한다.")
    @PostMapping("/{userId}/report")
    public void addUserReported(@PathVariable(name = "userId") String userId){
        String reporter = authService.getUserPrincipalOrThrow();

    }




    @Operation(summary = "사용자의 마지막 활동 시간", description = "마지막 활동 시간, 혹은 현재 온라인 상태라는 정보를 반환")
    @GetMapping("/{userId}/timestamp")
    public ResponseEntity<String> getUserLastActive(@Schema(example = "admin") @PathVariable(name = "userId") String userId){
        String lastActive = userInfoService.getUserLastActive(userId);
        return ResponseEntity.ok().body(lastActive);
    }


}
