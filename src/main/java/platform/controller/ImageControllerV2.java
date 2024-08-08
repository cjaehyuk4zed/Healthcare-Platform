package platform.controller;

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
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import platform.dto.ImageDTO;
import platform.dto.postdto.PostingImageDTO;
import platform.service.AuthService;
import platform.service.FileService;
import platform.service.URIService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import static platform.constants.DirectoryMapConstants.HOME_DIR;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v2/images")
@Tag(name = "ImagesV2", description = "이미지 관련 API")
public class ImageControllerV2 {

    private final FileService fileService;
    private final AuthService authService;
    private final URIService uriService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image saved & returned img src link", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostingImageDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "MultipartFile is empty"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @Operation(summary = "이미지 저장, image src 링크와 imageId 반환", description = "postId는 프런트엔드에서 전달받는다. 백엔드에서는 imageId 생성 및 이미지 파일 저장 후, 이미지 호출 API 의 src 링크와 imageId를 반환")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDTO.Response> savePostImage(HttpServletRequest request,
                                                @RequestPart(name = "image") MultipartFile multipartFile) throws IOException {
        log.info("ImageController POST /image : Content Type : {}", request.getContentType());
        if (multipartFile.isEmpty()) {
            log.info("multipartFile is empty");
            throw new BadRequestException("Image file was not sent to the server");
        }
        fileService.isImage(multipartFile);

        String imageId = UUID.randomUUID().toString();
        log.info("imageId : {}", imageId);
        String imageName = imageId + "." + multipartFile.getContentType().split("/")[1];
        String userId = authService.getUserPrincipalOrThrow();

        // Save a reference to the file in MySQL DB (file name and file path)
        ImageDTO imageDTO = new ImageDTO(imageId, imageName, userId, LocalDateTime.now());
        fileService.saveImage(imageDTO);

        // Save file to physical location on PC
        // Location is : HOME_DIR/userId/imageId.jpg (or png, etc.)
        Path imageFileDir = Path.of(HOME_DIR, userId);
        fileService.saveFile(multipartFile, imageFileDir ,imageName);

        String imageSrc = uriService.setImageURIByImageId(imageId);
        ImageDTO.Response response = new ImageDTO.Response(imageId, imageSrc);
        return ResponseEntity.ok().body(response);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Error deleting image")
    })
    @Operation(summary = "이미지 삭제", description = "DB와 local 저장소에 있는 이미지 삭제")
    @DeleteMapping(value = "/{imageId}")
    public ResponseEntity<String> deleteImage(@Schema(example = "19cf9b21-b154-4699-ad11-b8150c945ae7") @PathVariable(name = "imageId") String imageId) throws IOException {
        log.info("ImageController DELETE /{imageId}");

        fileService.deleteImage(imageId);
        return ResponseEntity.ok().body("Image deleted successfully");
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image file sent", content = {
                    @Content(mediaType = "image (jpg, png, etc)")
            }),
            @ApiResponse(responseCode = "404", description = "Image not found - Invalid Name")
    })
    @Operation(summary = "게시글 이미지 파일 호출 API (imageName 활용)", description = "userId, postId, imageName 으로 이미지 파일을 호출하는 API")
    @GetMapping(value = "/{imageId}")
    public ResponseEntity<Resource> getImage(@Schema(example = "19cf9b21-b154-4699-ad11-b8150c945ae7") @PathVariable(name = "imageId") String imageId) throws FileNotFoundException, BadRequestException {
        log.info("ImageController GET /{imageId}");
        ImageDTO imageDTO = fileService.findImage(imageId);

        Path imageFilePath = Path.of(HOME_DIR, imageDTO.getUserId(), imageDTO.getImageName());

        // Get MediaType by File Extension
        MediaType mediaType = MediaTypeFactory.getMediaType(imageDTO.getImageName())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        Resource resource = fileService.findFile(imageFilePath);

        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }

}
