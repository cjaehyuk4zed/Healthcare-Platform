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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import platform.dto.postdto.PostingCommentDTO;
import platform.dto.postdto.PostingImageDTO;
import platform.dto.postdto.PostingInfoDTO;
import platform.dto.postdto.PostInfoResponseDTO;
import platform.service.FileService;
import platform.service.PostingCommentService;
import platform.service.PostingInfoService;
import platform.service.TestService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v2/test")
@Tag(name = "TestsV2", description = "로그인 필요없는 더미 테스트용 API")
public class TestControllerV2 {

    private final PostingInfoService postingInfoService;
    private final PostingCommentService postingCommentService;
    private final FileService fileService;
    private final TestService testService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Post information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingInfoDTO.class))
            })
    })
    @Operation(summary = "랜덤 postId로 게시글 저장", description = "PostId를 랜덤값으로 생성한다. 더미데이터 생성용 API")
    @PostMapping("/post/save")
    public ResponseEntity<PostingInfoDTO> savePost(@RequestBody PostingInfoDTO.Save postSaveDTO) throws DataIntegrityViolationException {
        postSaveDTO.setPostingSaved(true);
        String uuid = UUID.randomUUID().toString();
        postSaveDTO.setPostingId(uuid);
        PostingInfoDTO postingInfoDTO = postingInfoService.savePost(postSaveDTO);
        return new ResponseEntity<>(postingInfoDTO, HttpStatus.OK);
    }

    @ResponseBody // For returning pure String data, can change later if needed to return a template
    @Operation(summary = "게시글 검색", description = "검색 결과에 해당되는 게시글 목록(List)을 반환. 카테고리, 제목+컨텐츠, 사용자 이름으로 검색할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingInfoDTO.class))
            })
    })
    @GetMapping("/post/search")
    public List<PostInfoResponseDTO> search(@Schema(example = "testcategory") @RequestParam(name = "category", required = false) String category,
                                    @Schema(example = "testsubcategory") @RequestParam(name = "subCategory", required = false) String subCategory,
                                    @Schema(example = "post1") @RequestParam(name = "titleAndContent", required = false) String titleAndContent,
                                    @Schema(example = "testuser") @RequestParam(name = "userId", required = false) String userId){
        // Dummy data category
        log.info("PostController /search has been initiated");
        List<PostInfoResponseDTO> postList;
        List<PostInfoResponseDTO> searchHitList;
        if(titleAndContent != null){
            log.info("if titleAndContent");
            if(category != null && subCategory != null){
                log.info("if titleAndContent + category");
                postList = postingInfoService.findAllByCategory(category, subCategory);
            }
            else{
                log.info("if titleAndContent + findAll");
                postList = postingInfoService.findAll();
            }
            searchHitList = postingInfoService.searchPostsByTitleAndContent(postList, titleAndContent);
            log.info("Search complete");
            return searchHitList;
        }
        if(userId != null){
            log.info("if userId");
            if(category != null && subCategory != null){
                log.info("if userId + category");
                searchHitList = postingInfoService.findAllByUserIdAndCategoryAndSubcategory(userId, category, subCategory);
            }
            else {
                log.info("if userId + findAll");
                searchHitList = postingInfoService.findAllByUserId(userId);
            }
            log.info("Search complete");
            return searchHitList;
        }
        else{
            return null;
        }
    }

    @Operation(summary = "<img> 태그에서 src 읽기 테스트")
    @GetMapping("/{userId}/{postId}/img-src")
    public ResponseEntity<List<String>> getImageSrc(@Schema(example = "admin") @PathVariable(name = "userId") String userId,
                                                  @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId) throws BadRequestException {
        PostInfoResponseDTO postInfoResponseDTO  = postingInfoService.findByPostingId(postId);
        log.info("PostController searchById : postInfoResponseDTO.getPostTitle() = " + postInfoResponseDTO.getTitle());

        List<String> imgTagList = testService.getImgTagList(postInfoResponseDTO);

        return ResponseEntity.ok().body(imgTagList);
    }

    @Operation(summary = "<span> 태그에서 src읽기 테스트")
    @GetMapping("/{userId}/{postId}/span-src")
    public ResponseEntity<List<String>> getSpanTag(@Schema(example = "admin") @PathVariable(name = "userId") String userId,
                                                    @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId) throws BadRequestException {
        PostInfoResponseDTO postInfoResponseDTO  = postingInfoService.findByPostingId(postId);
        log.info("PostController searchById : postInfoResponseDTO.getPostTitle() = " + postInfoResponseDTO.getTitle());
        log.info("Post Content : " + postInfoResponseDTO.getPostContent());

        List<String> spanTagList = testService.getSpanTagList(postInfoResponseDTO);

        return ResponseEntity.ok().body(spanTagList);
    }

    @Operation(summary = "게시글 댓글 조회", description = "특정 게시글 댓글 조회. 게시글 전체 댓글 조회 API는 따로 만들 예정이다.")
    @GetMapping("/{userId}/{postId}/comment/{commentId}")
    public ResponseEntity<PostingCommentDTO.Response> getComment(@Schema(example = "admin") @PathVariable(name = "userId") String userId,
                                                                 @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId,
                                                                 @Schema(example = "1") @PathVariable(name = "commentId") Long commentId) throws BadRequestException {
        if(!postingInfoService.isPostingExists(userId, postId)){
            throw new BadRequestException("This post doesn't exist");
        }

        PostingCommentDTO.Response response = postingCommentService.getComment(userId, commentId);

        return ResponseEntity.ok().body(response);
    }


}
