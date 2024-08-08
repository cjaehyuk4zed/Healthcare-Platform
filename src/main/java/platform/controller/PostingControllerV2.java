package platform.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import platform.domain.Posting_Attachment;
import platform.dto.ImageDTO;
import platform.dto.PageableDTO;
import platform.dto.postdto.PostingAttachmentDTO;
import platform.dto.postdto.PostingImageDTO;
import platform.dto.postdto.*;
import platform.service.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static platform.constants.DirectoryMapConstants.*;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v2/postings")
@Tag(name = "PostingsV2", description = "게시글 관련 API")
public class PostingControllerV2 {

    private final UserInfoService userInfoService;
    private final PostingInfoService postingInfoService;
    private final PostingCommentService postingCommentService;
    private final URIService uriService;
    private final FileService fileService;
    private final AuthService authService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Post information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingInfoDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid Ids"),
            @ApiResponse(responseCode = "500", description = "Illegal unquoted characters in Ids")
    })
    @Operation(summary = "포스팅 업로드 & 저장", description = "게시글 상세 정보까지 저장하고, <script> 태그 사전검사 및 삭제 로직이 구현되어 있다. User_Info 테이블에서 postCount 도 자동 업데이트 된다.")
    @PostMapping("/posting")
    public ResponseEntity<PostingInfoDTO> savePost(@RequestBody PostingInfoDTO.Save saveDTO)
            throws DataIntegrityViolationException, HttpMessageConversionException, BadRequestException {

        if(!authService.isCurrentUser(saveDTO.getUserId())){
            throw new BadRequestException("Login info does not match the post creator ID");
        }

        saveDTO.setPostingSaved(true);
        PostingInfoDTO postingInfoDTO = postingInfoService.savePost(saveDTO);
        return ResponseEntity.ok().body(postingInfoDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Post information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingInfoDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Post not found - Invalid postingId or userId")
    })
    @Operation(summary = "게시글 조회하기", description = "게시글에 대한 정보를 반환한다. 게시글이 존재하지 않을 경우 null 을 반환.")
    @GetMapping("/{postingId}")
    public ResponseEntity<PostInfoResponseDTO> searchById(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId) throws BadRequestException {
        PostInfoResponseDTO postInfoResponseDTO  = postingInfoService.findByPostingId(postingId);
        log.info("PostingController searchById : postInfoResponseDTO.getPostTitle() = {}", postInfoResponseDTO.getTitle());
        return ResponseEntity.ok().body(postInfoResponseDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Post not found for given userId and postingId")
    })
    @Operation(summary = "게시글 삭제하기", description = "현재 로그인 된 userId와 게시글 작성자 ID 가 일치하거나, admin 권한 계정으로 로그인 된 경우에만 삭제가 가능하다. User_Info 테이블에서 postCount 도 자동 업데이트 된다.")
    @DeleteMapping("/{postingId}")
    public ResponseEntity<String> deletePost(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId) throws IOException {
        log.info("PostingController /delete postingId : {}", postingId);
        postingInfoService.deletePost(postingId);
        return ResponseEntity.ok().body("Post deleted successfully");
    }

    @Operation(summary = "게시글 썸네일 지정하기", description = "postingId와 imageId 정보를 전달하면, 게시글의 다른 내용은 수정하지 않고 썸네일만 업데이트 한다.")
    @PostMapping("/{postingId}/thumbnail")
    public ResponseEntity<String> savePostingThumbnail (@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId,
                                                        @Schema(example = "1e0b6cce-6fb1-49bd-adac-53d43e151234") @RequestParam(name = "imageId") String imageId) throws BadRequestException {
        log.info("PostingController /{postingId}/thumbnail");

        //Check if the thumbnail file exists in the DB
        ImageDTO imageDTO = fileService.findImage(imageId);

        String imageSrc = uriService.setImageURIByImageName(imageDTO.getImageName());

        // Save thumbnail src link in MySQL DB (Posting_Info table)
        postingInfoService.updatePostThumbnail(imageDTO.getUserId(), postingId, imageSrc);

        return ResponseEntity.ok().body("Thumbnail updated successfully");
    }


/**
 *
 * 아래부터는 comment (포스팅 댓글) 관련 API들
 * 댓글 등록, 수정, 조회, 삭제 등
 *
 **/

    @Operation(summary = "게시글 댓글 추가", description = "댓글 id 는 백엔드에서 자동 생성한다. Hierarchical(계층적) 댓글 구조를 위해서, parentId는 프런트엔드에서 전달을 해줘야 한다. parent댓글이 없을 경우, parentId는 null이다")
    @PostMapping("/{postingId}/comment")
    public ResponseEntity<PostingCommentDTO.Response> addComment(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId,
                                                                 @RequestBody PostingCommentDTO.Request comment) throws BadRequestException {
        String userId = authService.getUserPrincipalOrThrow();
        if(!postingInfoService.isPostingExists(userId, postingId)){
            throw new BadRequestException("This post doesn't exist");
        }

        if(comment.getParentId() == 0){comment.setParentId(null);}
        PostingCommentDTO.Response response = postingCommentService.saveNewComment(comment, userId, postingId);

        return ResponseEntity.ok().body(response);
    }

    // 배열에서 첫 댓글이 상위 댓글, 그 아래에 추가 댓글이 있으면 그건 상위 댓글에 대한 대댓글들이다. 시간순으로 나열된 배열로 반환.
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "[댓글+대댓글]의 배열들을 반환한다. 각 배열의 첫 댓글(index 0)이 상위 댓글이며, 그 아래 댓글(index 1, 2...)이 해당 댓글의 대댓글들이다.")
    })
    @Operation(summary = "게시글 댓글 전체 조회", description = "해당 게시물의 모든 댓글 조회. Hierarchical 계층으로 대댓글 구조를, 시간 순서로 나열해서 조회한다.")
    @GetMapping("/{postingId}/comments")
    public ResponseEntity<List<List<PostingCommentDTO.Response>>> getComments(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId) throws BadRequestException {
        boolean isPostExists = postingInfoService.isPostingExists(postingId);
        if(!isPostExists){throw new BadRequestException("Post does not exist");}

        List<List<PostingCommentDTO.Response>> listList = postingCommentService.getComments(postingId);
        return ResponseEntity.ok(listList);
    }

    @Operation(summary = "특정 게시글 댓글 수정", description = "특정 댓글을 수정. 수정은 해당 댓글의 작성자 또는 어드민 계정만 할 수 있다")
    @PatchMapping("/{postingId}/comments/{commentId}")
    public ResponseEntity<PostingCommentDTO.Response> updateComment(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId,
                                                                    @Schema(example = "1") @PathVariable(name = "commentId") Long commentId,
                                                                    @RequestBody PostingCommentDTO.Edit request) throws BadRequestException, AccessDeniedException {
        if(!postingInfoService.isPostingExists(postingId)){
            throw new BadRequestException("This post doesn't exist");
        }
        PostingCommentDTO.Response response = postingCommentService.updateComment(request, commentId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "게시글 댓글 삭제", description = "댓글 작성자 ID와 현재 사용자 ID가 일치 하는지, 또는 어드민 계정인지 확인 후 삭제")
    @DeleteMapping("/{postingId}/comments/{commentId}")
    public ResponseEntity<PostingCommentDTO.Response> deleteComment(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId,
                                                                    @Schema(example = "1") @PathVariable(name = "commentId") Long commentId) throws BadRequestException, AccessDeniedException {
        if(!postingInfoService.isPostingExists(postingId)){
            throw new BadRequestException("This post doesn't exist");
        }
        String userId = authService.getUserPrincipalOrThrow();
        PostingCommentDTO.Response response = postingCommentService.deleteComment(commentId);

        return ResponseEntity.ok().body(response);
    }



    
/**
 *
 * 아래부터는 attachment (포스팅 첨부파일) 관련 API들
 * 첨부파일 추가, 다운로드, 삭제 등
 *
 **/

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment saved successfully"),
            @ApiResponse(responseCode = "500", description = "Error saving attachment file")
    })
    @Operation(summary = "게시글 첨부파일 추가하기", description = "첨부파일, userId, postId를 multipartFormData 로 수신하고 local storage 에 저장한다.")
    @PostMapping(value = "/{postingId}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostingAttachmentDTO.SrcLink> saveAttachment(@Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId,
                                                                       @RequestPart MultipartFile attachment) throws IOException {
        String userId = authService.getUserPrincipalOrThrow();
        if(!postingInfoService.isPostingCreator(userId, postingId)){
            throw new AccessDeniedException("You do not have permission to add attachments to this post");
        }

        String attachmentName = attachment.getOriginalFilename();
        Path fileDirectory = Path.of(HOME_DIR, userId, postingId);

        String attachmentId = UUID.randomUUID().toString();
        fileService.savePostAttachment(new PostingAttachmentDTO(attachmentName, attachmentId, userId, postingId, LocalDateTime.now()));
        fileService.saveFile(attachment, fileDirectory, attachmentName);

        String src = fileService.setPostAttachmentSrcLink(userId, postingId, attachmentId);
        PostingAttachmentDTO.SrcLink srcDTO = new PostingAttachmentDTO.SrcLink(src, attachmentId);

        return ResponseEntity.ok(srcDTO);
    }
    
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Error deleting image")
    })
    @Operation(summary = "게시글 첨부파일 삭제", description = "DB와 local 저장소에 있는 첨부파일 삭제")
    @DeleteMapping(value = "/{postingId}/attachments/{attachmentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> deletePostAttachment(HttpServletRequest request,
                                                       @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId,
                                                       @Schema(example = "19cf9b21-b154-4699-ad11-b8150c945ae7.jpeg") @PathVariable(name = "attachmentId") String attachmentId) throws IOException {
        String principal = authService.getUserPrincipalOrThrow();
        String postingCreatorId = postingInfoService.getPostCreatorByPostingId(postingId);
        if(!authService.isAdmin()){
            log.info("PostingController /postings/{postingId}/attachments/{attachmentId} : User is not ADMIN user");
            if(!principal.equals(postingCreatorId)){throw new AccessDeniedException("You do not have permission to delete attachments from this post");}
        }
        Path attachmentDir = Path.of(HOME_DIR, postingCreatorId, postingId);
        fileService.deleteFile(attachmentDir, attachmentId);

        return ResponseEntity.ok().body("Attachment deleted successfully");
    }

    @Operation(summary = "게시글 첨부파일 다운로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment downloaded successfully", content = {
                    @Content(mediaType = "application/octet-stream", examples = {@ExampleObject(value = "attachment;file")})}),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid IDs"),
            @ApiResponse(responseCode = "404", description = "Valid IDs, but file not found")
    })
    @GetMapping("/{postingId}/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId,
            @Schema(example = "0b09eebb-2ae4-4c49-ac34-b4cc5db14956") @PathVariable(name = "attachmentId") String attachmentId) throws BadRequestException, FileNotFoundException {

        String postingCreatorId = postingInfoService.getPostCreatorByPostingId(postingId);
        log.info("Posting Creator Id : {}", postingCreatorId);

        Posting_Attachment postAttachment = fileService.findPostAttachment(postingCreatorId, attachmentId);

        String attachmentName = postAttachment.getAttachmentName();
        MediaType mediaType = MediaTypeFactory.getMediaType(attachmentName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        Path filePath = Path.of(HOME_DIR, postingCreatorId, postingId, attachmentName);
        log.info("PostingController GET /{postingId}/attachments/{attachmentId} : mediaType : {} | filePath : {}", mediaType, filePath);
        Resource resource = fileService.findFile(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + URLEncoder.encode(attachmentName, StandardCharsets.UTF_8));
        headers.add(HttpHeaders.CONTENT_TYPE, mediaType.toString());
        return ResponseEntity.ok().headers(headers).contentType(mediaType).body(resource);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of attachments (with download links) - 첨부파일 목록 반환 (다운로드 링크 포함)", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingAttachmentDTO.Response.class))
            })
    })
    @Operation(summary = "게시글 첨부파일 목록 받기", description = "게시글 첨부파일 정보 및 첨부파일 다운로드 API src 링크까지 반환한다")
    @GetMapping("/{postingId}/attachments")
    public ResponseEntity<List<PostingAttachmentDTO.Response>> getAttachmentList(
            @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postingId") String postingId) throws BadRequestException {
        PostInfoResponseDTO postingInfo = postingInfoService.findByPostingId(postingId);
        List<PostingAttachmentDTO.Response> attachmentDTOList = fileService.findPostAttachments(postingInfo.getUserId(), UUID.fromString(postingId));
        if(attachmentDTOList.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        fileService.setPostAttachmentSrcLinks(attachmentDTOList);
        return ResponseEntity.ok().body(attachmentDTOList);
    }




/**
*
* 아래부터는 draft (임시 포스팅) 관련 API들
* 임시 포스팅 생성, 조회, 삭제 등
*
**/

    @Operation(summary = "임시 포스팅 업로드 & PostId 반환", description = "UserId를 보내주면, 해당 사용자에 대해 임시 게시글을 생성하고 postId를 반환한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return postId"),
            @ApiResponse(responseCode = "403", description = "Invalid userId")
    })
    @PostMapping("/draft")
    public ResponseEntity<String> createDraft() throws DataIntegrityViolationException{
        String userId = authService.getUserPrincipalOrThrow();
        String postId = postingInfoService.createDraft(userId);
        return ResponseEntity.ok().body(postId);
    }


    @Operation(summary = "임시 포스팅 저장", description = "임시 포스팅을, 임시 저장하는 것이다. 추후에 다시 불러올 수 있으며, 완전히 저장하기 전까지는 임시 포스팅 상태로 유지된다.")
    @PutMapping("/drafts/{postingId}")
    public ResponseEntity<String> saveDraft(@PathVariable(name = "postingId") String postingId,
                                            @RequestBody PostingDraftDTO postingDraftDTO) throws BadRequestException {
        if(!authService.isCurrentUser(postingDraftDTO.getUserId())){
            throw new AccessDeniedException("You do not have permission to save this posting draft");
        }
        if(!postingDraftDTO.getPostingId().equals(postingId)){
            throw new BadRequestException("Post not found - invalid Posting Id");
        }
        postingInfoService.saveDraft(postingDraftDTO);
        return ResponseEntity.ok().body("Draft saved");
    }

    @Operation(summary = "특정 임시 포스팅 불러오기", description = "특정 임시 게시글 불러오기. 수정하고, 완전히 저장을 할 수도 있고, 또는 다시 임시 저장 상태로 저장할 수도 있다.")
    @GetMapping("/drafts/{postingId}")
    public ResponseEntity<PostInfoResponseDTO> getDraft(@PathVariable(name = "postingId") String postingId) throws BadRequestException {

        PostInfoResponseDTO postInfoResponseDTO = postingInfoService.findByPostingId(postingId);
        return ResponseEntity.ok().body(postInfoResponseDTO);
    }

    @Operation(summary = "특정 임시 포스팅 삭제하기", description = "임시 포스팅 삭제하기. 삭제시 복구할 수 없다.")
    @DeleteMapping("/drafts/{postingId}")
    public void deleteDraft(@PathVariable(name = "postingId") String postingId) throws IOException {
        String postingCreatorId = postingInfoService.getPostCreatorByPostingId(postingId);
        log.info("Draft Creator Id : {}", postingCreatorId);

        if(!authService.isCurrentUser(postingCreatorId)){
            throw new AccessDeniedException("You do not have permission to delete this draft");
        }

        postingInfoService.deletePost(postingId);
    }


/**
 *
 * 아래부터는 search (게시글 검색) 또는 게시글 목록 불러오기 관련 API들
 * 특정 검색 필터들을 통해 포스팅 검색
 *
 **/

    @Operation(summary = "게시글 검색 결과를 프리뷰 목록으로 반환", description = "적용된 검색 필터에 알맞게 게시글 조회. 필터에 아무 조건을 입력하지 않아도 된다 (그 경우 각 필드의 default 값으로 검색한다.)")
    @GetMapping("/search")
    public ResponseEntity<List<PostingPreviewDTO>> searchPostsByQuery(@Schema(implementation = PostingSearchDTO.class, description = "각 검색 필터에 알맞는 조건 입력. 각 필드는 입력이 없거나 잘못된 경우, default값으로 검색된다.")
                                                                      PostingSearchDTO postingSearchDTO,
                                                                      @Schema(implementation = PageableDTO.class, description = "sort 속성에는 recent | likes | views 입력 (default는 recent). 그리고 sortOrder 속성에는 asc | desc 입력 (default는 desc)")
                                                                      PageableDTO pageableDTO){
        log.info("PostingController /get/search");
        Sort sort;
        if(pageableDTO.getSortOrder().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, pageableDTO.getSort());
        } else{
            sort = Sort.by(Sort.Direction.DESC, pageableDTO.getSort());
        }
        PageRequest pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getSize(), sort);

        log.info("PostingController /get/search : sort method : {}", pageable.getSort());
        List<PostingPreviewDTO> postingPreviewDTOS = postingInfoService.getPostsBySearchQueryPaged(postingSearchDTO, pageable);

        return ResponseEntity.ok().body(postingPreviewDTOS);
    }

    @Operation(summary = "특정 사용자의 게시글 검색 결과를 프리뷰 목록으로 반환", description = "적용된 검색 필터에 알맞게 게시글 조회. 필터에 아무 조건을 입력하지 않아도 된다 (그 경우 각 필드의 default 값으로 검색한다.)")
    @GetMapping("/user")
    public ResponseEntity<List<PostingPreviewDTO>> findAllByUserId(@RequestParam(name = "userId") String userId,
                                                                      @Schema(implementation = PageableDTO.class, description = "sort 속성에는 recent | likes | views 입력 (default는 recent). 그리고 sortOrder 속성에는 asc | desc 입력 (default는 desc)")
                                                                      PageableDTO pageableDTO){
        log.info("PostingController GET /postings/user");
        Sort sort;
        if(pageableDTO.getSortOrder().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, pageableDTO.getSort());
        } else{
            sort = Sort.by(Sort.Direction.DESC, pageableDTO.getSort());
        }
        PageRequest pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getSize(), sort);

        log.info("PostingController GET /postings/user : sort method : {}", pageable.getSort());
        // Search only by userId : Gets all postings by this user
        PostingSearchDTO postingSearchDTO = new PostingSearchDTO(null, null, null, userId);
        List<PostingPreviewDTO> postingPreviewDTOS = postingInfoService.getPostsBySearchQueryPaged(postingSearchDTO, pageable);

        return ResponseEntity.ok().body(postingPreviewDTOS);
    }

    @Operation(summary = "특정 카테고리의 게시글들을 프리뷰 목록으로 반환", description = "적용된 검색 필터에 알맞게 게시글 조회. 필터에 아무 조건을 입력하지 않아도 된다 (그 경우 각 필드의 default 값으로 검색한다.)")
    @GetMapping("/category")
    public ResponseEntity<List<PostingPreviewDTO>> findAllByCategory(@RequestParam(name = "category") String category,
                                                                      @Schema(implementation = PageableDTO.class, description = "sort 속성에는 recent | likes | views 입력 (default는 recent). 그리고 sortOrder 속성에는 asc | desc 입력 (default는 desc)")
                                                                      PageableDTO pageableDTO){
        log.info("PostingController GET /category");
        Sort sort;
        if(pageableDTO.getSortOrder().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, pageableDTO.getSort());
        } else{
            sort = Sort.by(Sort.Direction.DESC, pageableDTO.getSort());
        }
        PageRequest pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getSize(), sort);

        log.info("PostingController GET /category : sort method : {}", pageable.getSort());
        // Search only by userId : Gets all postings by this user
        PostingSearchDTO postingSearchDTO = new PostingSearchDTO(category, null, null, null);
        List<PostingPreviewDTO> postingPreviewDTOS = postingInfoService.getPostsBySearchQueryPaged(postingSearchDTO, pageable);

        return ResponseEntity.ok().body(postingPreviewDTOS);
    }
    
    @Operation(summary = "특정 카테고리의 게시글들을 프리뷰 목록으로 반환", description = "적용된 검색 필터에 알맞게 게시글 조회. 필터에 아무 조건을 입력하지 않아도 된다 (그 경우 각 필드의 default 값으로 검색한다.)")
    @GetMapping("/subcategory")
    public ResponseEntity<List<PostingPreviewDTO>> findAllBySubcategory(@RequestParam(name = "subcategory") String subcategory,
                                                                      @Schema(implementation = PageableDTO.class, description = "sort 속성에는 recent | likes | views 입력 (default는 recent). 그리고 sortOrder 속성에는 asc | desc 입력 (default는 desc)")
                                                                      PageableDTO pageableDTO){
        log.info("PostingController GET /subcategory");
        Sort sort;
        if(pageableDTO.getSortOrder().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, pageableDTO.getSort());
        } else{
            sort = Sort.by(Sort.Direction.DESC, pageableDTO.getSort());
        }
        PageRequest pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getSize(), sort);

        log.info("PostingController GET /subcategory : sort method : {}", pageable.getSort());
        // Search only by userId : Gets all postings by this user
        PostingSearchDTO postingSearchDTO = new PostingSearchDTO(null, subcategory, null, null);
        List<PostingPreviewDTO> postingPreviewDTOS = postingInfoService.getPostsBySearchQueryPaged(postingSearchDTO, pageable);

        return ResponseEntity.ok().body(postingPreviewDTOS);
    }

    @Operation(summary = "모든 게시글을 프리뷰 목록으로 반환", description = "적용된 검색 필터에 알맞게 게시글 조회. 필터에 아무 조건을 입력하지 않아도 된다 (그 경우 각 필드의 default 값으로 검색한다.)")
    @GetMapping("")
    public ResponseEntity<List<PostingPreviewDTO>> findAll(@Schema(implementation = PageableDTO.class, description = "sort 속성에는 recent | likes | views 입력 (default는 recent). 그리고 sortOrder 속성에는 asc | desc 입력 (default는 desc)")
                                                               PageableDTO pageableDTO){
        log.info("PostingController GET /");
        Sort sort;
        if(pageableDTO.getSortOrder().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, pageableDTO.getSort());
        } else{
            sort = Sort.by(Sort.Direction.DESC, pageableDTO.getSort());
        }
        PageRequest pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getSize(), sort);

        log.info("PostingController GET / : sort method : {}", pageable.getSort());
        // Search only by userId : Gets all postings by this user
        PostingSearchDTO postingSearchDTO = new PostingSearchDTO(null, null, null, null);
        List<PostingPreviewDTO> postingPreviewDTOS = postingInfoService.getPostsBySearchQueryPaged(postingSearchDTO, pageable);

        return ResponseEntity.ok().body(postingPreviewDTOS);
    }

    @Hidden
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image file sent", content = {
                    @Content(mediaType = "image (jpg, png, etc)")
            }),
            @ApiResponse(responseCode = "404", description = "Image not found - Invalid Name")
    })
    @Operation(summary = "게시글 이미지 파일 호출 API (imageName 활용)", description = "userId, postId, imageName 으로 이미지 파일을 호출하는 API")
    @GetMapping(value = "/{userId}/{postId}/images/{imageName}")
    public ResponseEntity<Resource> getImageByName(
            @Schema(example = "admin") @PathVariable(name = "userId") String userId,
            @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId,
            @Schema(example = "19cf9b21-b154-4699-ad11-b8150c945ae7.jpeg") @PathVariable(name = "imageName") String imageName) throws FileNotFoundException {
        Path imageFilePath = Path.of(HOME_DIR, userId, postId, imageName);

        // Get MediaType by File Extension
        MediaType mediaType = MediaTypeFactory.getMediaType(imageName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = fileService.findFile(imageFilePath);

        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @Hidden
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image file sent", content = {
                    @Content(mediaType = "image (jpg, png, etc)")
            }),
            @ApiResponse(responseCode = "404", description = "Image not found - Invalid Name")
    })
    @Operation(summary = "기본값 이미지 파일들 호출 API (imageName 활용)", description = "기본 이미지들을 imageName 으로 이미지 파일을 호출하는 API")
    @GetMapping(value = "/default/images/{imageName}")
    public ResponseEntity<Resource> getDefaultImages(
            @Schema(example = "defaultThumbnail.png") @PathVariable(name = "imageName") String imageName) throws FileNotFoundException {
        Path imageFilePath = Path.of(HOME_DIR, "default", imageName);

        // Get MediaType by File Extension
        MediaType mediaType = MediaTypeFactory.getMediaType(imageName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = fileService.findFile(imageFilePath);
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @Hidden
    @Operation(summary = "게시글 즐겨찾기에 추가 버튼", description = "즐겨찾기에 추가 클릭 후 결과를 문자열로 반환 (즐겨찾기 추가 성공 / 즐겨찾기에 이미 있음 메시지)")
    @PutMapping("/{userId}/{postId}/fav")
    public ResponseEntity<String> favPost(@Schema(example = "admin") @PathVariable(name = "userId") String userId,
                                          @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId) throws AccessDeniedException {
        log.info("PUT PostingController /fav");
//        postingInfoService.validatePostAuthorIds(userId, postId, postAuthorDTO);

        String principal = authService.getUserPrincipalOrThrow();

        boolean userFaved = userInfoService.userFavPostToggle(principal, postId, userId);
        if(userFaved){
            postingInfoService.updatePostFaves(postId, true);
            return ResponseEntity.ok().body("You added this post to favorites");
        }
        return ResponseEntity.ok().body("This post is already in your favorites");
    }

    @Hidden
    @Operation(summary = "게시글 즐겨찾기에서 삭제 버튼", description = "즐겨찾기에서 삭제 클릭 후 결과를 문자열로 반환 (즐겨찾기에서 삭제 성공 메시지).")
    @DeleteMapping("/{userId}/{postId}/fav")
    public ResponseEntity<String> favPostRemove(@Schema(example = "admin") @PathVariable(name = "userId") String userId,
                                                @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId) throws AccessDeniedException {
        log.info("DELETE PostingController /fav");
//        postingInfoService.validatePostAuthorIds(userId, postId, postAuthorDTO);

        String principal = authService.getUserPrincipalOrThrow();

        boolean userFaved = userInfoService.userFavPostToggle(principal, postId, userId);
        if(userFaved){
            postingInfoService.updatePostFaves(postId, false);
        }
        return ResponseEntity.ok().body("Post is no longer in your favorites");
    }

    @Operation(summary = "게시글 공개/비공개 설정 전환", description = "공개/비공개 설정값을 true/false 값으로 반환. 공개면 true, 비공개면 false 값을 반환.")
    @PatchMapping("/public")
    public ResponseEntity<Boolean> isPublicPost(@Schema(example = "00000000-0000-0000-0000-000000000000") @RequestBody String postId) throws BadRequestException, AccessDeniedException {
        log.info("PostingController /public");
//        postingInfoService.validatePostAuthorIds(userId, postId, postAuthorDTO);
        String principal = authService.getUserPrincipalOrThrow();

        log.info("Principal : {} , postID : {}", principal, postId);
        boolean postPublic = postingInfoService.updatePostPublic(principal, postId);

        return ResponseEntity.ok().body(postPublic);
    }

}
