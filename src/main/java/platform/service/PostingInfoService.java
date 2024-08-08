package platform.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import platform.domain.Posting_Info;
import platform.domain.User_Interest;
import platform.dto.postdto.*;
import platform.repository.PostInfoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import platform.repository.UserInterestRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.constants.DirectoryMapConstants.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PostingInfoService {

    private final FileService fileService;
    private final UserInfoService userInfoService;
    private final AuthService authService;
    private final HtmlTagService htmlTagService;
    private final PostInfoRepository postInfoRepository;
    private final ModelMapper modelMapper;
    private final UserInterestRepository userInterestRepository;

    public boolean isPostingExists(String userId, String postId) throws IllegalArgumentException{
        UUID postUuid = UUID.fromString(postId);
        int count = postInfoRepository.countByUserIdAndPostingId(userId, postUuid);
        return count > 0;
    }

    public boolean isPostingExists(String postingId) throws IllegalArgumentException{
        UUID postUuid = UUID.fromString(postingId);
        int count = postInfoRepository.countByPostingId(postUuid);
        return count > 0;
    }

    public boolean isPostingCreator(String userId, String postingId) throws BadRequestException {
        UUID postingUuid = UUID.fromString(postingId);
        String postingCreatorId = postInfoRepository.findUserIdByPostingId(postingUuid)
                .orElseThrow(() -> new BadRequestException("Post not found - Invalid Posting Id"));
        if(authService.isCurrentUser(postingCreatorId)){
            return true;
        } else {return false;}
    }

    public List<PostInfoResponseDTO> findAll(){
//        return postInfoRepository.findAll();
        return postInfoRepository.findAll()
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostInfoResponseDTO.class))
                .collect(Collectors.toList());
    }

    // List of Posts within a certain category
    public List<PostInfoResponseDTO> findAllByCategory(String category, String subcategory){
        return postInfoRepository.findIdsByCategory(category, subcategory);
    }

    public List<PostInfoResponseDTO> findAllByUserIdAndCategoryAndSubcategory(String userId, String category, String subcategory){
        return postInfoRepository.findAllByUserIdAndCategoryAndSubcategory(userId, category, subcategory)
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostInfoResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<PostInfoResponseDTO> findAllByUserId(String userId){
        return postInfoRepository.findAllByUserId(userId)
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostInfoResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<PostInfoResponseDTO.Draft> findAllDraftsByUserId(String userId){
        return postInfoRepository.findAllByUserIdAndPostingSaved(userId, false)
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostInfoResponseDTO.Draft.class))
                .collect(Collectors.toList());
    }

    public PostInfoResponseDTO findByPostingId(String postId) throws BadRequestException {
        UUID postUuid = UUID.fromString(postId);
        Posting_Info postInfo = postInfoRepository.findByPostingId(postUuid)
                .orElseThrow(() -> new BadRequestException("Post not found for postId : " + postId));
        log.info("postInfo.getPostId() & getPostTitle() = {} & {}", postInfo.getPostingId(), postInfo.getTitle());
        // 게시글 조회시, 게시글 총 조회수 (views) + 1 증가
        // 하지만 반복적으로 조회시에 views가 증가하지 않도록 예외처리가 필요한데

        postInfo.setViews(postInfo.getViews()+1);
        postInfoRepository.save(postInfo);
        return modelMapper.map(postInfo, PostInfoResponseDTO.class);
    }

    public String getPostCreatorByPostingId(String postingId) throws BadRequestException {
        UUID postingUuid = UUID.fromString(postingId);
        String postingCreator = postInfoRepository.findUserIdByPostingId(postingUuid)
                .orElseThrow(() -> new BadRequestException("Posting not found - invalid Ids"));
        return postingCreator;
    }

    public List<PostInfoResponseDTO> searchPostsByTitleAndContent(List<PostInfoResponseDTO> postList, String titleAndContent){
        log.info("PostingInfoService searchPostsByTitleAndContent");
        List<PostInfoResponseDTO> searchHitList = new ArrayList<>();
        for(PostInfoResponseDTO postInfo : postList) {
            boolean searchHit = false;
            if(postInfo.getPostContent()!=null){
                List<String> spanTextList = htmlTagService.getSpanTagList(postInfo.getPostContent());
                if(spanTextList.isEmpty()){continue;}
                searchHit = htmlTagService.searchSpanTagList(spanTextList, titleAndContent);
            }
            if(searchHit){searchHitList.add(postInfo);}
        }
        return searchHitList;
    }

    public String createDraft(String userId) throws DataIntegrityViolationException {
        PostingInfoDTO.Save saveDTO = new PostingInfoDTO.Save();
        String postId;
        // Check to make sure that the postId does not overlap with an existing postId
        while(true){
            postId = UUID.randomUUID().toString();
            if(!isPostingExists(userId, postId)){break;}
        }
        saveDTO.setPostingId(postId);
        saveDTO.setUserId(userId);
        saveDTO.setTitle("Untitled");
        saveDTO.setTimestamp(LocalDateTime.now());
        savePost(saveDTO);
        return postId;
    }

    public void saveDraft(PostingDraftDTO postingDraftDTO) throws DataIntegrityViolationException {
        PostingInfoDTO.Save saveDTO = modelMapper.map(postingDraftDTO, PostingInfoDTO.Save.class);
        savePost(saveDTO);
    }

    public PostInfoResponseDTO getDraftByPostingId(String postingId) throws BadRequestException {
        UUID postUuid = UUID.fromString(postingId);
        Posting_Info postInfo = postInfoRepository.findByPostingId(postUuid)
                .orElseThrow(() -> new BadRequestException("Post not found for postId : " + postingId));
        log.info("postInfo.getPostId() & getPostTitle() = {} & {}", postInfo.getPostingId(), postInfo.getTitle());

        return modelMapper.map(postInfo, PostInfoResponseDTO.class);
    }

    // 게시글 저장 후, User_Info 테이블에서 사용자의 총 게시글 수 (post_count)도 업데이트
    public PostingInfoDTO savePost(PostingInfoDTO.Save postSaveDTO) throws DataIntegrityViolationException {
        log.info("PostingInfoService savePost");

        // Set default thumbnail if no thumbnail has been set by the user
        if(postSaveDTO.getThumbnail() == null || !postSaveDTO.getThumbnail().contains("file-controller")){
            String defaultThumbnail = PLATFORM_SERVER_SOCKET_ADDR + POSTING_CONTROLLER + "/default/default/images/defaultThumbnail.png";
            postSaveDTO.setThumbnail(defaultThumbnail);
        }

        // Delete <script> tags from the content to prevent SQL injection attacks
        if(postSaveDTO.getPostingContent()!=null){
            postSaveDTO.setPostingContent(htmlTagService.deleteScriptTags(postSaveDTO.getPostingContent()));
        }

        // Get existing postInfo entity from DB and map elements from the saveDTO.
        // Only merges matching elements, as "setSkipNullEnabled(true)" is configured in ModelMapperConfig
        Posting_Info postInfo = modelMapper.map(postSaveDTO, Posting_Info.class);

        // Set date created if this is the first time saving this post
        if(postInfo.isPostingSaved() && postInfo.getTimestamp() == null){
            postInfo.setTimestamp(LocalDateTime.now());
        }

        // Set readTime - 250 words is approximately 1 minutes of reading
        List<String> spanTagList = htmlTagService.getSpanTagList(postInfo.getPostingContent());
        int readTime = (spanTagList.size() / 250) + 1;
        postInfo.setReadTime(readTime);

        try {
            postInfoRepository.save(postInfo);
            log.info("PostingInfoService savePost : Post saved successfully");
            userInfoService.addPostCount(postSaveDTO.getUserId());

            return modelMapper.map(postInfo, PostingInfoDTO.class);
        }
        catch(DataIntegrityViolationException e) {
            log.info("User does not exist for userId : {}", postSaveDTO.getUserId());
            throw new DataIntegrityViolationException("User does not exist for userId : " + postSaveDTO.getUserId());
        }
    }

    public List<Posting_Info> findDrafts() {
        List<Posting_Info> drafts = postInfoRepository.findAllByPostingSaved(false);
        if(drafts.isEmpty()){
            return null;
        }
        return drafts;
    }

    public boolean deleteDrafts() throws IOException {
        List<Posting_Info> postings = findDrafts();
        if(postings == null || postings.isEmpty()){
            return false;
        } else{
            for(Posting_Info postingInfo : postings){
                deletePost(postingInfo);
            }
            return true;
        }
    }

    public void deletePost(String postId) throws IOException, AccessDeniedException {
        Posting_Info postingInfo = postInfoRepository.findByPostingId(UUID.fromString(postId))
                .orElseThrow(() -> new BadRequestException("Post not found - Invalid Posting Id"));
        deletePost(postingInfo);
    }

    // 게시글 삭제시, 게시글 작성자의 postCount 는 1 감소
    private void deletePost(Posting_Info postingInfo) throws IOException {
        log.info("PostingInfoService deletePost");
        if(!authService.isCurrentUser(postingInfo.getUserId()) && !authService.isAdmin()){
            throw new AccessDeniedException("You do not have permission to delete this post");
        }
        // 게시글 이미지와 첨부파일 삭제
        fileService.deletePostImages(postingInfo);
        fileService.deletePostAttachments(postingInfo);

        // 게시글 로컬 폴더 삭제
        Path fileDir = Path.of(HOME_DIR, postingInfo.getUserId(), postingInfo.getPostingId().toString());
        fileService.deleteDir(fileDir);

        // 게시글 삭제, 작성자의 postCount 는 1 감소
        postInfoRepository.delete(postingInfo);
        userInfoService.subPostCount(postingInfo.getUserId());
    }

    // Repository 에서 Page 객체를 전달 받은 것을, stream으로 변환하여 DTO로 mapping 한 후에는 List로 변환한다.
    // 프런트엔드에 전달하는 입장에서는 굳이 Page 객체를 받을 이유가 없기 때문. 또한 Collectors.toPage 같은 메서드는 없다.
    // 이미 stream 으로 변환하여 처리한 값을, 다시 페이지로 만드는 것을 비효율적이며 그럴 이유도 없다. List로 반환하면 된다.
    public List<PostingPreviewDTO> getPostsBySearchQueryPaged(PostingSearchDTO postingSearchDTO, Pageable pageable){
        log.info("PostingInfoService getRecentPostsBySearchQueryPaged");
        if(pageable.getPageNumber() < 0){throw new IllegalArgumentException("Invalid page number");}
        if(pageable.getPageSize() <= 0){throw new IllegalArgumentException("Invalid page size");}

        if(postingSearchDTO.getQuery()==null || postingSearchDTO.getQuery().isEmpty()){
            return postInfoRepository.findBySearchQueryPaged(postingSearchDTO, pageable)
                    .stream()
                    .map(postInfo -> modelMapper.map(postInfo, PostingPreviewDTO.class))
                    .collect(Collectors.toList());
        }

        log.info("PostingInfoService getRecentPostsBySearchQueryPaged : Search query {}", postingSearchDTO.getQuery());
        List<Posting_Info> postInfos = postInfoRepository.findBySearchQueryList(postingSearchDTO, pageable);
        return getPagedList(postInfos, postingSearchDTO.getQuery(), pageable)
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostingPreviewDTO.class))
                .collect(Collectors.toList());
    }

    public List<PostingPreviewDTO> getPostsBySortAndInterest(String userId, Pageable pageable){
        log.info("PostingInfoService getPostsBySortAndInterest");

        // Interaction with UserInterestRepository for recommendations
        List<User_Interest> userInterests = userInterestRepository.findAllByUserInterestCompositeKey_UserIdAndUserInterested(userId, true);

        // 관심 카테고리가 없을 경우, 기본 정렬 기준의 목록 반환
        if(userInterests.isEmpty()) {return getPostsBySort(pageable);}

        return postInfoRepository.findBySortAndInterest(pageable, userInterests)
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostingPreviewDTO.class))
                .collect(Collectors.toList());
    }

    public List<PostingPreviewDTO> getPostsBySort(Pageable pageable){
        log.info("PostingInfoService getPostsBySort");
        return postInfoRepository.findBySort(pageable)
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostingPreviewDTO.class))
                .collect(Collectors.toList());
    }

    public List<PostingPreviewDTO> getPopularPostsBySearchQueryPaged(PostingSearchDTO postingSearchDTO, Pageable pageable){
        log.info("PostingInfoService getPopularPostsBySearchQueryPaged");
        if(pageable.getPageNumber() < 0){throw new IllegalArgumentException("Invalid page number");}
        if(pageable.getPageSize() <= 0){throw new IllegalArgumentException("Invalid page size");}

        if(postingSearchDTO.getQuery()==null || postingSearchDTO.getQuery().isEmpty()){
            return postInfoRepository.findBySearchQueryPaged(postingSearchDTO, pageable)
                    .stream()
                    .map(postInfo -> modelMapper.map(postInfo, PostingPreviewDTO.class))
                    .collect(Collectors.toList());
        }

        log.info("PostingInfoService getPopularPostsBySearchQueryPaged : Search titleAndContent query");
        List<Posting_Info> postInfos = postInfoRepository.findBySearchQueryList(postingSearchDTO, pageable);
        return getPagedList(postInfos, postingSearchDTO.getQuery(), pageable)
                .stream()
                .map(postInfo -> modelMapper.map(postInfo, PostingPreviewDTO.class))
                .collect(Collectors.toList());
    }

    public List<Posting_Info> getPagedList(List<Posting_Info> postInfos, String query, Pageable pageable){
        log.info("PostingInfoService getPagedList");
        List<Posting_Info> searchHitList = new ArrayList<>();
        for(Posting_Info postInfo : postInfos) {
            if(postInfo.getTitle().toLowerCase().contains(query.toLowerCase()) || postInfo.getSubtitle().toLowerCase().contains(query.toLowerCase())){
                searchHitList.add(postInfo);
                continue;
            }
            List<String> spanTextList = htmlTagService.getSpanTagList(postInfo.getPostingContent());
            log.info(postInfo.getTimestamp().toString());
            if (spanTextList.isEmpty()) {
                continue;
            }
            boolean searchHit = htmlTagService.searchSpanTagList(spanTextList, query);
            if (searchHit) {
                searchHitList.add(postInfo);
            }
        }

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int lastIndex = (pageable.getPageNumber() + 1) * pageable.getPageSize();

        if(startIndex >= searchHitList.size()){
            return new ArrayList<>(); // Return empty list
        }
        if(lastIndex >= searchHitList.size()){
// .size() can be used as the lastIndex, as the lastIndex is exclusive! (startIndex is inclusive)
            return searchHitList.subList(startIndex, searchHitList.size());
        }
        return searchHitList.subList(startIndex, lastIndex);
    }

    public void updatePostLikes(String postId, boolean userLiked){
        try{
            UUID postUuid = UUID.fromString(postId);
            postInfoRepository.updateLikes(postUuid, userLiked);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid postId");
        }
    }

    public void updatePostFaves(String postId, boolean userFaved){
        try{
            UUID postUuid = UUID.fromString(postId);
            postInfoRepository.updateFaves(postUuid, userFaved);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid postId");
        }
    }

    public boolean updatePostPublic(String userId, String postId) throws BadRequestException {
        log.info("PostingInfoService updatePostPublic");
        try{
            log.info("Principal : {} , postID : {}", userId, postId);
            UUID postUuid = UUID.fromString(postId);
            Posting_Info postInfo = postInfoRepository.findByPostingIdAndUserId(postUuid, userId)
                    .orElseThrow(() -> new BadRequestException("Post not found for postId : " + postId + " | userId : " + userId));

            log.info("go to set posting public");
            postInfo.setPostingPublic(!postInfo.isPostingPublic());
            postInfoRepository.save(postInfo);
            return postInfo.isPostingPublic();
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid postId");
        }
    }

    public void updatePostThumbnail(String userId, String postId, String imgSrc) {
        UUID postUuid = UUID.fromString(postId);
        postInfoRepository.updateThumbnail(userId, postUuid, imgSrc);
    }
}
