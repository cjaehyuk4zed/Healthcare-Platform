package platform.service;

import org.apache.tika.Tika;
import org.springframework.security.access.AccessDeniedException;
import platform.domain.Image;
import platform.domain.Posting_Attachment;
import platform.domain.Posting_Info;
import platform.domain.keys.PostingAttachmentCompositeKey;
import platform.dto.ImageDTO;
import platform.dto.postdto.PostingAttachmentDTO;
import platform.repository.AttachmentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import platform.repository.ImageRepository;
import platform.repository.PostInfoRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.constants.DirectoryMapConstants.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class FileService {

    private final PostInfoRepository postInfoRepository;
    private final ImageRepository imageRepository;
    private final AttachmentRepository attachmentRepository;
    private final URIService uriService;
    private final HtmlTagService htmlTagService;
    private final AuthService authService;
    private final ModelMapper modelMapper;


    public void saveFile(MultipartFile multipartFile, Path directory, String fileName) throws IOException {
        log.info("FileService saveFile");
        Path filePath = Path.of(directory.toString(), fileName);
        if(!Files.exists(directory)){
            try {
                Files.createDirectories(directory);
                log.info("FileService saveFile - Directory created successfully");
            } catch (IOException e){
                throw new IOException("Error creating directory");
            }
        }
        try {
            Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            throw new IOException("Error saving file to directory");
        }
    }

    public void deleteFile(Path directory, String fileName) throws IOException {
        log.info("FileService deleteFile");
        Path filePath = Path.of(directory.toString(), fileName);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if(!deleted){
                log.info("FileService deleteFile : FILE COULD NOT BE DELETED : FILE DOES NOT EXIST");
                throw new FileNotFoundException("File could not be deleted : File does not exist");
            }
        } catch (IOException e){
            throw new IOException("FileService deleteFile - IOException Error deleting file");
        }
    }

    public void deleteDir(Path directory) throws IOException{
        try {
            boolean deleted = Files.deleteIfExists(directory);
            if(!deleted){
                log.info("FileService deleteDir : DIRECTORY COULD NOT BE DELETED : DIRECTORY DOES NOT EXIST");
            }
        } catch (IOException e) {
            throw new IOException("FileService deleteDir - IOException Error deleting directory, most likely directory is NOT EMPTY");
        }
    }

    public Resource findFile(Path filePath) throws FileNotFoundException{
        log.info("FileService findFile");
        Resource resource = new FileSystemResource(filePath);
        if(resource.exists() && resource.isReadable()){
            log.info("FileService findFile - Resource exists & is readable");
            return resource;
        }
        else {
            throw new FileNotFoundException("Requested file could not be found");
        }
    }

    public void savePostAttachment(PostingAttachmentDTO postingAttachmentDTO){
        postingAttachmentDTO.setTimestamp(LocalDateTime.now());
        Posting_Attachment postAttachment = modelMapper.map(postingAttachmentDTO, Posting_Attachment.class);
        log.info("FileService savePostAttachment = {}", postAttachment.toString());
        attachmentRepository.save(postAttachment);
    }

    public Posting_Attachment findPostAttachment(String userId, String attachmentId) throws BadRequestException {
        log.info("FileService findPostAttachment");
        UUID attachmentUuid;
        try {
            attachmentUuid = UUID.fromString(attachmentId);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid attachmentId");
        }
        PostingAttachmentCompositeKey postingAttachmentCompositeKey = new PostingAttachmentCompositeKey(attachmentUuid, userId);
//        log.info("FileService findPostAttachment composite key : " + postingAttachmentCompositeKey.toString());
        Posting_Attachment postAttachment = attachmentRepository.findById(postingAttachmentCompositeKey)
                .orElseThrow(() -> new BadRequestException("Invalid userId or attachmentId"));
        log.info("FileService findPostAttachment = {}", postAttachment);
        return postAttachment;
    }

    public List<PostingAttachmentDTO.Response> findPostAttachments(String userId, UUID postId){
        return attachmentRepository.findAllByPostingAttachmentCompositeKey_UserIdAndPostingId(userId, postId)
                .stream()
                .map(postAttachment -> modelMapper.map(postAttachment, PostingAttachmentDTO.Response.class))
                .collect(Collectors.toList());
    }

    public void setPostAttachmentSrcLinks(List<PostingAttachmentDTO.Response> attachmentDTOList){
        for (PostingAttachmentDTO.Response dto : attachmentDTOList){
            String attachmentSrc = setPostAttachmentSrcLink(dto.getUserId(), dto.getPostingId(), dto.getAttachmentId());
            dto.setAttachmentSrc(attachmentSrc);
        }
    }

    public String setPostAttachmentSrcLink(String userId, String postId, String attachmentId){
        log.info("FileService setPostAttachmentSrcLink");
        return PLATFORM_SERVER_SOCKET_ADDR + POSTING_CONTROLLER + "/" + userId + "/" + postId + "/attachments/" + attachmentId;
    }

    // search img tag
    // delete images
    public void deletePostImages(Posting_Info postingInfo) throws IOException{
//        List<PostingImageDTO> postImages = postingImageRepository.findAllByPostingId(postingInfo.getPostingId());
        if(postingInfo.getPostingContent().isEmpty()){
            return;
        }

        List<UUID> imageIds = getImageIdList(postingInfo);
        if(imageIds.isEmpty()){
            return;
        }

        for(UUID imageId : imageIds){
            deleteImage(imageId.toString());
            imageRepository.deleteById(imageId);
        }
    }

    public List<UUID> getImageIdList(Posting_Info postingInfo){
        List<String> imgTagSrcList = htmlTagService.getImgTagSrcList(postingInfo);
        if(imgTagSrcList.isEmpty()){
            return new ArrayList<>();
        }
        List<UUID> imageIds = new ArrayList<>();

        for(String src : imgTagSrcList){
            String[] srcSplit = src.split(".");
            UUID imageId = UUID.fromString(srcSplit[srcSplit.length - 2]);
            imageIds.add(imageId);
        }

        return imageIds;
    }

    public void deleteListPostsAttachments(List<Posting_Info> posts) throws IOException {
        log.info("FileService deletePostAttachments");
        for(Posting_Info p : posts){
            deletePostAttachments(p);
        }
    }

    public void deletePostAttachments(String postId, String userId) throws IOException {
        Posting_Info postInfo = postInfoRepository.findByPostingIdAndUserId(UUID.fromString(postId), userId)
                .orElseThrow(() -> new BadRequestException("Post not found for postId : " + postId + " | userId : " + userId));
        deletePostAttachments(postInfo);

    }

    public void deletePostAttachments(Posting_Info postInfo) throws IOException{
        List<Posting_Attachment> postAttachments =  attachmentRepository.findAllByPostingId(postInfo.getPostingId());
        if(postAttachments != null) {
            for(Posting_Attachment a : postAttachments){
                Path fileDir = Path.of(HOME_DIR, a.getPostingAttachmentCompositeKey().getUserId(), a.getPostingId().toString());
                deleteFile(fileDir, a.getAttachmentName());
            }
            attachmentRepository.deleteAlLByPostingId(postInfo.getPostingId());
        } else {
            log.info("FileService deletePostAttachments : No attachments found");
        }
    }

    public String getFileExtension(String fileNameOrFilePath) throws BadRequestException {
        System.out.println("fileNameOrFilePath = " + fileNameOrFilePath);
        return Optional.ofNullable(fileNameOrFilePath)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileNameOrFilePath.lastIndexOf(".") + 1))
                .orElseThrow(() -> new BadRequestException("FileService getFileExtension - File not found, invalid file path"));
        // lastIndexOf(".") + 1 을 해줘야 file extension 이름만 나온다 (.jpg 대신 jpg)
    }

    public void isAttachment(MultipartFile file) throws IOException{
        log.info("FileService isAttachment");
        try {
            InputStream stream = file.getInputStream();
            Tika tika = new Tika();

            String mimeType = tika.detect(stream);
            if(!mimeType.equals(file.getContentType().toString())){throw new IOException("File content does not match file extension");}
        } catch (IOException e){
            throw new IOException("File is invalid");
        }
    }



    /**
    * APIs related to ImageController
    * Deals with saving images to DB
    * as well as saving images to physical storage as files
    * */


    // Check if file is actually an image, and the file content matches the file extension
    public void isImage(MultipartFile file) throws IOException{
        log.info("FileService isImage");
        try {
            InputStream stream = file.getInputStream();
            Tika tika = new Tika();

            String mimeType = tika.detect(stream);
            if(!mimeType.startsWith("image/")){throw new IOException("File is not an image file");}
            if(!mimeType.equals(file.getContentType().toString())){throw new IOException("File content does not match file extension");}
        }
        catch (IOException e){
            throw new IOException("File is invalid");
        }
    }

    public void saveImage(ImageDTO imageDTO){
        log.info("FileService saveImage");
        Image image = modelMapper.map(imageDTO, Image.class);
        image.setSaved(true);
        imageRepository.saveAndFlush(image); // save postImage and immediately flush changes to the DB, instead of persisting the data in JPA asynchronously
    }

    public void deleteImage(String imageId) throws IOException {
        UUID imageUuid = UUID.fromString(imageId);
        log.info("FileService deleteImage");
        Image image = imageRepository.findByImageId(imageUuid)
                .orElseThrow(() -> new BadRequestException("Image not found - Invalid image Id"));

        if(!authService.isCurrentUser(image.getUserId()) && !authService.isAdmin()){
            throw new AccessDeniedException("You do not have permission to delete this image");
        }

        String imageName = image.getImageName();
        Path imageDir = Path.of(HOME_DIR, image.getUserId());
        deleteFile(imageDir, imageName);

        imageRepository.delete(image);
        log.info("Image {} deleted successfully", imageName);
    }

    public ImageDTO findImage(String imageId) throws BadRequestException {
        UUID imageUuid = UUID.fromString(imageId);
        Image image = imageRepository.findByImageId(imageUuid)
                .orElseThrow(() -> new BadRequestException("Image not found - Invalid IDs"));
        return modelMapper.map(image, ImageDTO.class);
    }
}
