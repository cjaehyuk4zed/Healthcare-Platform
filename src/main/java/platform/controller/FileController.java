package platform.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import platform.dto.postdto.PostingAttachmentDTO;
import platform.dto.postdto.PostingImageDTO;
import platform.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static platform.constants.DirectoryMapConstants.*;

@Controller
@AllArgsConstructor
@RequestMapping("/api/file")
@Slf4j
@Tag(name = "Files", description = "(outdated) File 입출력 API")
@Hidden
public class FileController {

    private final FileService fileService;

    // Resource is from org.springframework.core.io.Resource NOT JAKARTA.ANNOTATION.RESOURCE
    // {fileName} needs to include the file extension as well (e.g. /send/index.html)
    // TEST API, delete when it's no longer needed
    // TEST API, delete when it's no longer needed
    @Operation(summary = "!!!테스트용 코드!!! Send file by file name", hidden = true)
    @GetMapping("/send/{userId}/{fileName}")
    public ResponseEntity<Resource> sendFile(@PathVariable String fileName) {
        log.info("Received File Name : " + fileName);
        String html_dir = HOME_DIR + "html/";
        Path filePath = Paths.get(html_dir).resolve(fileName);
        log.info("File Path : " + filePath);
        Resource resource = new FileSystemResource(filePath);
        log.info("Resource : " + resource);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image file sent", content = {
                    @Content(mediaType = "image (jpg, png, etc)")
            }),
            @ApiResponse(responseCode = "404", description = "Image not found - Invalid Name")
    })
    @Operation(summary = "GET /api/v2/file-controller/users/{userId}/posts/{postId}/image/{imageName} 로 이동", description = "userId, postId, imageName 으로 이미지 파일을 호출하는 API")
    @GetMapping(value = "/get/image/{userId}/{postId}/name/{imageName}")
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image file sent", content = {
                    @Content(mediaType = "image (jpg, png, etc)")
            }),
            @ApiResponse(responseCode = "404", description = "Image not found - Invalid Name")
    })
    @Operation(summary = "GET /api/v2/file-controller/default/images/{imageName} 로 이동", description = "기본 이미지들을 imageName 으로 이미지 파일을 호출하는 API")
    @GetMapping(value = "/get/image/default/name/{imageName}")
    public ResponseEntity<Resource> getDefaultImages(
            @Schema(example = "defaultThumbnail.png") @PathVariable(name = "imageName") String imageName) throws FileNotFoundException {
        Path imageFilePath = Path.of(HOME_DIR, "default", imageName);

        // Get MediaType by File Extension
        MediaType mediaType = MediaTypeFactory.getMediaType(imageName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = fileService.findFile(imageFilePath);

        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment saved successfully"),
            @ApiResponse(responseCode = "500", description = "Error saving attachment file")
    })
    @Operation(summary = "POST /api/v2/file-controller/users/{userId}/posts/{postId}/attachments 로 이동", description = "첨부파일, userId, postId를 multipartFormData 로 수신하고 local storage 에 저장한다.")
    @PostMapping(value = "/attachment/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostingAttachmentDTO.SrcLink> saveAttachment(
            @RequestPart MultipartFile attachment,
            @Schema(example = "admin") @RequestPart String userId,
            @Schema(example = "00000000-0000-0000-0000-000000000000") @RequestPart String postId) throws IOException {
        String attachmentName = attachment.getOriginalFilename();
        Path fileDirectory = Path.of(HOME_DIR, userId, postId);

        String attachmentId = UUID.randomUUID().toString();
        fileService.savePostAttachment(new PostingAttachmentDTO(attachmentName, attachmentId, userId, postId, null));
        fileService.saveFile(attachment, fileDirectory, attachmentName);

        String src = fileService.setPostAttachmentSrcLink(userId, postId, attachmentId);
        PostingAttachmentDTO.SrcLink srcDTO = new PostingAttachmentDTO.SrcLink(src, attachmentId);

        return ResponseEntity.ok(srcDTO);
    }


//    @Operation(summary = "GET /api/v2/file-controller/users/{userId}/posts/{postId}/attachments/{attachmentId} 로 이동")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Attachment downloaded successfully", content = {
//                    @Content(mediaType = "application/octet-stream", examples = {@ExampleObject(value = "attachment;file")})}),
//            @ApiResponse(responseCode = "400", description = "Bad Request - invalid IDs"),
//            @ApiResponse(responseCode = "404", description = "Valid IDs, but file not found")
//    })
//    @GetMapping("/get/attachment/download/{userId}/{postId}/{attachmentId}")
//    public ResponseEntity<Resource> downloadAttachment(
//            @Schema(example = "admin") @PathVariable(name = "userId") String userId,
//            @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId,
//            @Schema(example = "ffc71ef4-4048-413c-83f4-be2c40a59728") @PathVariable(name = "attachmentId") String attachmentId) throws BadRequestException, FileNotFoundException {
//
//        Posting_Attachment postAttachment = fileService.findPostAttachment(userId, attachmentId);
//        String attachmentName = postAttachment.getAttachmentName();
//        MediaType mediaType = MediaTypeFactory.getMediaType(attachmentName)
//                .orElse(MediaType.APPLICATION_OCTET_STREAM);
//        log.info("FileController /attachment/download : mediaType : " + mediaType);
//        Path filePath = Path.of(HOME_DIR, userId, postId, attachmentName);
//        log.info("FileController /attachment/download : filePath : " + filePath);
//        Resource resource = fileService.findFile(filePath);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment;filename=" + URLEncoder.encode(attachmentName, StandardCharsets.UTF_8));
//        headers.add(HttpHeaders.CONTENT_TYPE, mediaType.toString());
//
//        return ResponseEntity.ok().headers(headers).contentType(mediaType).body(resource);
//    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of attachments (with download links) - 첨부파일 목록 반환 (다운로드 링크 포함)", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostingAttachmentDTO.Response.class))
            })
    })
    @Operation(summary = "GET /api/v2/file-controller/users/{userId}/posts/{postId}/attachments 로 이동", description = "게시글 첨부파일 정보 및 첨부파일 다운로드 API src 링크까지 반환한다")
    @GetMapping("/get/attachment/list/{userId}/{postId}")
    public ResponseEntity<List<PostingAttachmentDTO.Response>> getAttachmentList(
            @Schema(example = "admin") @PathVariable(name = "userId") String userId,
            @Schema(example = "00000000-0000-0000-0000-000000000000") @PathVariable(name = "postId") String postId){

        List<PostingAttachmentDTO.Response> attachmentDTOList = fileService.findPostAttachments(userId, UUID.fromString(postId));
        if(attachmentDTOList.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        fileService.setPostAttachmentSrcLinks(attachmentDTOList);
        return ResponseEntity.ok().body(attachmentDTOList);
    }
}
