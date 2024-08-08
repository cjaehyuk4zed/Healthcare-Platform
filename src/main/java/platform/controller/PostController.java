package platform.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import platform.auth.Role;
import platform.dto.postdto.*;
import platform.service.AuthService;
import platform.service.FileService;
import platform.service.PostingInfoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import platform.service.UserInfoService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/post")
@Tag(name = "Posts", description = "(outdated) 게시글 관련 API")
@Hidden
public class PostController {

    private final UserInfoService userInfoService;
    private final PostingInfoService postingInfoService;
    private final FileService fileService;
    private final AuthService authService;

    @Operation(summary = "!!!테스트용 코드!!! 게시글 HTML 텍스트 전송 테스트 용도.", hidden = true)
    @PostMapping("/save-test")
    public ResponseEntity<String> saveTest(@RequestBody String postData){
        PostingInfoDTO postingInfoDTO = new PostingInfoDTO();
        LocalDateTime localDateTime = postingInfoDTO.getTimestamp();
        return ResponseEntity.ok().body("success");
    }

    // Return to a universal GetMapping method for retrieving post_info data

    @Operation(summary = "POST /api/v2/post-management/users/{userId}/posts 로 변경", description = "UserId를 보내주면, 해당 사용자에 대해 임시 게시글을 생성하고 postId를 반환한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return postId"),
            @ApiResponse(responseCode = "403", description = "Invalid userId")
    })
    @PostMapping("/draft/{userId}")
    public ResponseEntity<String> saveDraft(@Schema(example = "admin") @PathVariable(value = "userId") String userId) throws DataIntegrityViolationException {
        String postId = postingInfoService.createDraft(userId);
        return ResponseEntity.ok().body(postId);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Post information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingInfoDTO.class))
            })
    })
    @Operation(summary = "POST /api/v2/post-management/users/{userId}/posts/{postId} 로 이동", description = "게시글 상세 정보까지 저장하고, <script> 태그 사전검사 및 삭제 로직이 구현되어 있다.")
    @PostMapping("/save")
    public ResponseEntity<PostingInfoDTO> savePost(@RequestBody PostingInfoDTO.Save postSaveDTO) throws DataIntegrityViolationException {
        postSaveDTO.setPostingSaved(true);
        PostingInfoDTO postingInfoDTO = postingInfoService.savePost(postSaveDTO);
        return new ResponseEntity<>(postingInfoDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Post information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingInfoDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Post not found - Invalid postId or userId")
    })
    @Operation(summary = "GET /api/v2/post-management/users/{userId}/posts/{postId} 로 이동", description = "게시글에 대한 정보를 반환한다. 게시글이 존재하지 않을 경우 null을 반환.")
    @GetMapping("/get/{userId}/{postId}")
    public ResponseEntity<PostInfoResponseDTO> searchById(@Schema(example = "admin") @PathVariable(name = "userId") String userId,
                                                  @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId) throws BadRequestException {
        PostInfoResponseDTO postInfoResponseDTO  = postingInfoService.findByPostingId(postId);
        log.info("PostController searchById : postInfoResponseDTO.getPostTitle() = " + postInfoResponseDTO.getTitle());
        return ResponseEntity.ok().body(postInfoResponseDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Post not found for given userId and postId")
    })
    @Operation(summary = "DELETE /api/v2/post-management/users/{userId}/posts/{postId} 로 이동", description = "현재 로그인 된 userId와 게시글 작성자ID가 일치하거나, admin 권한 계정으로 로그인 된 경우에만 삭제가 가능하다.")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePost(@RequestParam(name = "userId") String userId,
                                             @RequestParam(name = "postId") String postId) throws IllegalArgumentException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth.getPrincipal().toString();
        log.info("PostController /delete auth : " + auth);
        if(principal==null){
            log.info("PostController /delete SecurityContextHolder principal is null");
            return ResponseEntity.badRequest().body("Please login and try again.");
        }

        if(auth.getAuthorities().stream().map(s -> s.toString())
                .noneMatch(s -> s.equals("ROLE_" + Role.ADMIN_MAIN) || s.equals("ROLE_" + Role.ADMIN_SUB))){
            log.info("PostController /delete : Not ADMIN user");
            if(!principal.equals(userId)){
                log.info("PostController /delete : Current userId does not match the post author userId");
                return ResponseEntity.badRequest().body("User : " + userId + " is not the owner of this post");
            }
        }
        log.info("PostController /delete postId : " + postId + " | userId : " + userId);
        postingInfoService.deletePost(postId);
        return ResponseEntity.ok().body("Post deleted successfully");
    }

    @Operation(summary = "GET /api/v2/post-management/posts/search 로 이동", description = "적용된 검색 필터에 알맞게 게시글 조회. Pageable 객체의 속성은 다음과 같다 (asc=오름차순, desc=내림차순 : " +
            "{\"page\": 0, \"size\": 10, \"sort\": [\"recent\"]} 여기서 sort 속성에 들어갈 단어는 프런트엔드와 통일해야 한다.")
    @GetMapping("/get/search")
    public ResponseEntity<List<PostingPreviewDTO>> getRecentPostsByQuery(PostingSearchDTO postingSearchDTO, Pageable pageable)
//                                                                      @RequestPart(name = "page") @Schema(example = "0", description = "페이지 번호") int page,
//                                                                      @RequestPart(name = "size") @Schema(example = "10", description = "페이지 크기 (항목 개수)") int size,
//                                                                      @RequestPart(name = "sort") @Schema(example = "recent", description = "정렬 순서. recent, views, likes 중 선택.") String sort)
    {
        log.info("PostController /get/search");
//        PageRequest pageable = PageRequest(page, size, sort);

        log.info("PostController /get/search : sort method : " + pageable.getSort());
        List<PostingPreviewDTO> postingPreviewDTOS = postingInfoService.getPostsBySearchQueryPaged(postingSearchDTO, pageable);

        return ResponseEntity.ok().body(postingPreviewDTOS);
    }

//    @Operation(summary = "게시글 좋아요 버튼", description = "좋아요 클릭 후 결과를 true/false 값으로 반환. 좋아요면 true, 좋아요 취소이면 false 값을 반환.")
//    @PostMapping("/like")
//    public ResponseEntity<Boolean> likePost(@RequestBody PostAuthorDTO postAuthorDTO) throws BadRequestException {
//        log.info("PostController /like");
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth == null){
//            throw new AccessDeniedException("You must be logged in to like a post!");
//        }
//        String principal = auth.getPrincipal().toString();
//        boolean userLiked = userInfoService.userLikesPostToggle(principal, postAuthorDTO.getPostId(), postAuthorDTO.getUserId());
//        postingInfoService.updatePostLikes(postAuthorDTO.getUserId(), postAuthorDTO.getPostId(), userLiked);
//
//        return ResponseEntity.ok().body(userLiked);
//    }

}
