package platform.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import platform.domain.*;
import platform.domain.keys.UserInterestCompositeKey;
import platform.dto.CategoryDTO;
import platform.dto.postdto.PostingPreviewDTO;
import platform.dto.userdto.UserInfoDTO;
import platform.dto.userdto.UserInterestDTO;
import platform.dto.userdto.UserTabDTO;
import platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static platform.constants.DirectoryMapConstants.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserLikesRepository userLikesRepository;
    private final UserFavRepository userFavRepository;
    private final UserFollowerRepository userFollowerRepository;
    private final UserInterestRepository userInterestRepository;
    private final PostInfoRepository postInfoRepository;
    private final UserTabRepository userTabRepository;

    private final RequestLogRepository requestLogRepository;

    private final AuthService authService;
    private final URIService uriService;
    private final ModelMapper modelMapper;

    public void saveUser(UserInfoDTO userInfoDTO){
        userInfoDTO.setTimestamp(LocalDate.now());
        User_Info userInfo = modelMapper.map(userInfoDTO, User_Info.class);
        userInfoRepository.save(userInfo);
    }

    public void updateUser(UserInfoDTO.Save userInfoDTO) throws BadRequestException {
        User_Info user = userInfoRepository.findById(userInfoDTO.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found - Invalid user ID"));
        modelMapper.map(userInfoDTO, user);
        userInfoRepository.save(user);
    }


    public UserInfoDTO.Response findUserProfile(String userId) throws BadRequestException {
        log.info("UserInfoService findUserProfile");
        User_Info userInfo = findUser(userId);
        UserInfoDTO.Response userInfoDTO = modelMapper.map(userInfo, UserInfoDTO.Response.class);
        // Send API endpoints for acquiring user profile pic and qr code
        userInfoDTO.setQrCode(uriService.setUserQrCodeURI(userId));
        userInfoDTO.setUserPic(uriService.setUserProfilePicURI(userId));
        log.info("UserInfoService findUserProfile : QR Code and Profile-Pic links set");
        return userInfoDTO;
    }

    public UserInfoDTO.Basic findUserProfileBasic(String userId) throws BadRequestException{
        log.info("UserInfoService findUserProfileBasic");
        User_Info userInfo = findUser(userId);
        UserInfoDTO.Basic userInfoBasicDTO = modelMapper.map(userInfo, UserInfoDTO.Basic.class);
        // Send API endpoints for acquiring user profile pic and qr code
        userInfoBasicDTO.setQrCode(uriService.setUserQrCodeURI(userId));
        userInfoBasicDTO.setUserPic(uriService.setUserProfilePicURI(userId));
        log.info("UserInfoService findUserProfileBasic : QR Code and Profile-Pic links set");
        return userInfoBasicDTO;
    }

    private User_Info findUser(String userId) throws BadRequestException {
        log.info("UserInfoService findUser");
        User_Info userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("UserInfoService findUser - User not found, invalid ID"));
        return userInfo;
    }

    public void saveQrCode(String userId) throws WriterException, IOException {
        try {
            String fileName = userId + "_qr.png";
            Path filePath = Path.of(HOME_DIR, userId, fileName);

            if(!Files.exists(filePath)){
                try {
                    Files.createDirectories(filePath);
                    log.info("Directory created successfully");
                } catch (Exception e){
                    throw new IOException("UserInfoService saveQrCode - Error creating directory");
                }
            }

            String siteURL = "https://allofhealth.net/user/marino";
            // test URL, change later to :
// String siteURL = "website main URL/profile/" + userId; or whatever the user profile page URL is
            // test URL, change later to :
// String siteURL = "website main URL/profile/" + userId; or whatever the user profile page URL is
            // test URL, change later to :
// String siteURL = "website main URL/profile/" + userId; or whatever the user profile page URL is
            // test URL, change later to :
// String siteURL = "website main URL/profile/" + userId; or whatever the user profile page URL is
            // test URL, change later to :
// String siteURL = "website main URL/profile/" + userId; or whatever the user profile page URL is
            // test URL, change later to :
// String siteURL = "website main URL/profile/" + userId; or whatever the user profile page URL is
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(siteURL, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
        } catch (WriterException e) { // ZXing library - QRCodeWriter exception
            throw new WriterException("UserInfoService saveQrCode - WriterException Error generating QR code");
        } catch (IOException e){
            throw new IOException("UserInfoService saveQrCode - IOException Error saving QR code");
        }
    }


    public Resource findQrCode(String userId) throws FileNotFoundException{
        String qrName = userId + "_qr.png";
        Path qrFilePath = Path.of(HOME_DIR, userId, qrName);

//      FileSystemResource 에서 exception handling 자동 처리
        Resource resource = new FileSystemResource(qrFilePath);
        if(resource.exists() && resource.isReadable()){
            log.info("QR code exists & is readable");
            return resource;
        }
        else {
            throw new FileNotFoundException("User QR code not found, invalid user ID");
        }
    }

    public void saveUserPic(MultipartFile multipartFile, Path directory, String fileName) throws IOException {
        Path filePath = Path.of(directory.toString(), fileName);
        if(!Files.exists(directory)){
            try {
                Files.createDirectories(directory);
                log.info("UserInfoService saveUserPic - Directory created successfully");
            } catch (Exception e){
                throw new IOException("Error creating user directory");
            }
        }
        else { log.info("Directory already exists");}

        // Delete existing profile pic, in case the file extension is different
        File[] matchingFiles = getMatchingFiles(directory, fileName).orElse(null);
        if(matchingFiles!= null){
            log.info("FileService saveUserPic : matching user profile pic = {}", matchingFiles[0]);
            Path path = Path.of(matchingFiles[0].getPath());
            log.info("path = {}", path);
            Files.delete(path);
        } else {
            log.info("FileService saveUserPic : No matching user profile pic found");
        }
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public String setUserProfilePicSrc(String userId){
        String userProfilePicSrc = uriService.setUserProfilePicURI(userId);
        return userProfilePicSrc;
    }

    // EDIT TO GET ANY IMG MEDIA TYPES!!!
    // EDIT TO GET ANY IMG MEDIA TYPES!!!
    // STUDY HOW THIS CODE WORKS PLEASE
    public Resource findUserPic(String userId) throws FileNotFoundException {
        String fileName = userId + "_pic";
        Path fileDirectory = Path.of(HOME_DIR, userId);
        File[] matchingFiles = getMatchingFiles(fileDirectory, fileName).orElse(null);
        if(matchingFiles != null){
    //      FileSystemResource 에서 exception handling 자동 처리
            Resource resource = new FileSystemResource(matchingFiles[0]);
            if(resource.exists() && resource.isReadable()){
                log.info("User pic exists & is readable");
                return resource;
            } else{
                throw new FileNotFoundException("User profile pic file not found");
            }
        }
        Path defaultUserPicPath = Path.of(DEFAULT_DIR, "defaultUserPic.png");
        Resource defaultUserPic = new FileSystemResource(defaultUserPicPath);
        return defaultUserPic;
    }

    private static Optional<File[]> getMatchingFiles(Path fileDirectory, String fileName) {
        File f = fileDirectory.toFile();
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(fileName);
            }
        });
        return Optional.ofNullable(matchingFiles);
    }

    public boolean userFavPostToggle(String userId, String postId, String postCreatorId) throws IllegalArgumentException {
        try{
            UUID postUuid = UUID.fromString(postId);
            User_Fav userFaves = userFavRepository.findByUserIdAndPostingId(userId, postUuid)
                    .orElse(new User_Fav(userId, postUuid, false));

            userFaves.setPostingFaved(!userFaves.isPostingFaved());
            userFavRepository.save(userFaves);
            return userFaves.isPostingFaved();
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid PostId provided");
        }
    }

    public boolean userLikesPost(String userId, String postId) throws IllegalArgumentException{
        try{
            UUID postUuid = UUID.fromString(postId);
            User_Like userLikes = userLikesRepository.findByUserIdAndPostingId(userId, postUuid)
                    .orElse(new User_Like(userId, postUuid, false));

            // If the post was not yet liked
            if(!userLikes.isPostingLiked()){
                userLikes.setPostingLiked(!userLikes.isPostingLiked());
                userLikesRepository.save(userLikes);
                return true;
            }
            // If the post was already liked
            log.info("User already liked this post");
            return false;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid PostId provided");
        }
    }

    public boolean deleteUserLikesPost(String userId, String postId) throws IllegalArgumentException{
        try{
            UUID postUuid = UUID.fromString(postId);
            User_Like userLikes = userLikesRepository.findByUserIdAndPostingId(userId, postUuid)
                    .orElse(null);

            // If post like was removed
            if(userLikes!=null){
                userLikesRepository.delete(userLikes);
                return true;
            }
            // If there was no post like to be removed
            log.info("User didn't like this post to start with");
            return false;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid PostId provided");
        }
    }

    public boolean isUserLikedPost(String userId, String postId){
        try{
            UUID postUuid = UUID.fromString(postId);
            User_Like userLiked = userLikesRepository.findByUserIdAndPostingId(userId, postUuid)
                    .orElse(null);
            if(userLiked == null){ return false;}
            return true;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid PostId provided");
        }
    }

    public List<PostingPreviewDTO> getUserLikedPosts(String userId){
        log.info("UserInfoService getUserLikedPosts");
        List<User_Like> userLikes = userLikesRepository.findByUserIdAndPostingLiked(userId, true);
        List<PostingPreviewDTO> postingPreviewDTOS = new ArrayList<>();
        List<UUID> postUUIDs = new ArrayList<>();

        if(userLikes.isEmpty()){
            // returning empty list
            return postingPreviewDTOS;
        }

        for(User_Like u : userLikes){
            postUUIDs.add(u.getPostingId());
        }

        for(UUID postingUUID : postUUIDs){
            Posting_Info postingInfo = postInfoRepository.findByPostingId(postingUUID)
                    .orElse(null);
            if(postingInfo == null){continue;}
            postingPreviewDTOS.add(modelMapper.map(postingInfo, PostingPreviewDTO.class));
        }

        return postingPreviewDTOS;
    }


    public boolean userFavPost(String userId, String postId, String postCreatorId) throws IllegalArgumentException {
        try{
            UUID postUuid = UUID.fromString(postId);
            User_Fav userFaves = userFavRepository.findByUserIdAndPostingId(userId, postUuid)
                    .orElse(new User_Fav(userId, postUuid, false));

            userFaves.setPostingFaved(!userFaves.isPostingFaved());
            userFavRepository.save(userFaves);
            return userFaves.isPostingFaved();
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid PostId provided");
        }
    }

    public boolean deleteUserFavPost(String userId, String postId) throws IllegalArgumentException {
        try{
            UUID postUuid = UUID.fromString(postId);
            User_Fav userFaves = userFavRepository.findByUserIdAndPostingId(userId, postUuid)
                    .orElse(null);


            // If post was removed from user's favorites list
            if(userFaves!=null){
                userFavRepository.delete(userFaves);
                return true;
            }
            // If post was not in user's favorites list to start with
            log.info("This post was not in your favorites list");
            return false;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid PostId provided");
        }
    }

    public void addUserFollower(String userId, String followerId){
        User_Follower userFollower = new User_Follower(userId, followerId);
        userFollowerRepository.save(userFollower);
        userInfoRepository.updateFollowingCount(userId, true);
        userInfoRepository.updateFollowerCount(followerId, true);
    }

    public void deleteUserFollower(String userId, String followerId){
        User_Follower userFollower = userFollowerRepository.findByUserIdAndFollowerId(userId, followerId)
                .orElse(null);
        if(userFollower == null){return;}

        userFollowerRepository.delete(userFollower);
        userInfoRepository.updateFollowingCount(userId, false);
        userInfoRepository.updateFollowerCount(followerId, false);
    }

    public boolean isUserFollower(String userId, String followerId){
        User_Follower userFollower = userFollowerRepository.findByUserIdAndFollowerId(userId, followerId)
                .orElse(null);
        if(userFollower == null){return false;}
        return true;
    }

    // 현자 사용자(userId)를 팔로잉 하고 있는 "follower" 들의 목록을 불러온다.
    public List<String> getUserFollowers(String userId){
        List<User_Follower> userFollowers = userFollowerRepository.findByUserId(userId);

        List<String> followers = new ArrayList<>();
        for(User_Follower u : userFollowers){
            followers.add(u.getFollowerId());
        }
        return followers;
    }

    // 역으로, 현재 사용자가 "follower"이고, 팔로잉 하는 중인 "userId"의 목록을 불러온다.
    public List<String> getUserFollowingList(String followerId){
        List<User_Follower> userFollowers = userFollowerRepository.findByFollowerId(followerId);

        List<String> followingList = new ArrayList<>();
        for(User_Follower u : userFollowers){
            followingList.add(u.getUserId());
        }
        return followingList;
    }


    public void addPostCount(String userId){
        userInfoRepository.updatePostingCount(userId, true);
    }

    public void subPostCount(String userId){
        userInfoRepository.updatePostingCount(userId, false);
    }

    public void saveUserInterest(String userId, CategoryDTO categoryDTO){
        if(!authService.isCurrentUser(userId) && !authService.isAdmin()){ throw new AccessDeniedException("You cannot edit this user's interests category");}

        UserInterestDTO.Save userInterestDTO = new UserInterestDTO.Save(userId, categoryDTO.getCategory(), categoryDTO.getSubcategory(), true);
        User_Interest userInterest = modelMapper.map(userInterestDTO, User_Interest.class);

        log.info("addUserInterest : {} | {} | {} | {}", userInterest.getUserInterestCompositeKey(),
                userInterest.getUserInterestCompositeKey().getUserId(),
                userInterest.getCategory(),
                userInterest.getUserInterestCompositeKey().getSubcategory());
        userInterestRepository.save(userInterest);
    }

    public void deleteUserInterest(String userId, CategoryDTO categoryDTO){
        if(!authService.isCurrentUser(userId) && !authService.isAdmin()){ throw new AccessDeniedException("You cannot edit this user's interests category");}

        UserInterestDTO.CompositeKey compositeKey = new UserInterestDTO.CompositeKey(userId, categoryDTO.getCategory(), categoryDTO.getSubcategory());
        UserInterestCompositeKey userInterestCompositeKey = modelMapper.map(compositeKey, UserInterestCompositeKey.class);

        User_Interest userInterest = userInterestRepository.findById(userInterestCompositeKey)
                .orElseThrow(() -> new DataIntegrityViolationException(""));

//        log.info("composite key : {}", userInterestCompositeKey.getUserId());
        log.info("deleteUserInterest : {} | {} | {} | {}", userInterest.getUserInterestCompositeKey(),
                userInterest.getUserInterestCompositeKey().getUserId(),
                userInterest.getCategory(),
                userInterest.getUserInterestCompositeKey().getSubcategory());
        userInterestRepository.delete(userInterest);
    }

    public void addUserInterest(String userId, CategoryDTO categoryDTO){
        log.info("UserInfoService addUserInterest");
        UserInterestCompositeKey compositeKey = new UserInterestCompositeKey(userId, categoryDTO.getCategory(), categoryDTO.getSubcategory());
        
        // 이미 존재하면 호출하기, 아닐 경우 새로 생성
        User_Interest userInterest = userInterestRepository.findById(compositeKey)
                .orElse(new User_Interest(compositeKey, false, 0));
        userInterest.setLikes(userInterest.getLikes() + 1);
        // 기존 정보에 likes +1, 또는 새로 생성된 정보 저장
        userInterestRepository.save(userInterest);
    }

    public void subUserInterest(String userId, CategoryDTO categoryDTO) throws BadRequestException {
        log.info("UserInfoService subUserInterest");
        UserInterestCompositeKey compositeKey = new UserInterestCompositeKey(userId, categoryDTO.getCategory(), categoryDTO.getSubcategory());

        User_Interest userInterest = userInterestRepository.findById(compositeKey)
                .orElseThrow(() -> new BadRequestException("User has not liked a post of this category/subcategory"));
        // 해당 카테고리에 대한 likes -1 감소, 존재하지 않을 경우 error 처리
        userInterest.setLikes(userInterest.getLikes() - 1);
        userInterestRepository.save(userInterest);
    }

    public List<CategoryDTO> getUserInterests(String userId){
        log.info("UserInfoService getUserInterests");
        List<User_Interest> userInterests = userInterestRepository.findAllByUserInterestCompositeKey_UserIdAndUserInterested(userId, true);

        List<CategoryDTO> categoryDTOS = new ArrayList<>();

        for(User_Interest u : userInterests){
            categoryDTOS.add(modelMapper.map(u, CategoryDTO.class));
        }
        return categoryDTOS;
    }

    public String getUserLastActive(String userId) {
        Request_Log requestLog = requestLogRepository.findFirstByUserIdOrderByTimestampDesc(userId)
                .orElse(null);
        if(requestLog==null){return "No login records";}
        else{
            String lastActive;
            Duration duration = Duration.between(requestLog.getTimestamp(), LocalDateTime.now());
            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            long minutes = duration.toMinutes() % 60;

            if(days > 0 ){lastActive = "Last active : " + days + "d ago";}
            else if(hours > 0){lastActive =  "Last active : " + hours + "h ago";}
            else if(minutes > 0){
                if(minutes <= 2){lastActive = "ONLINE";}
                else{lastActive =  "Last active : " + minutes + "m ago";}
            }
            else {lastActive =  "ONLINE";}

            return lastActive;
        }
    }


    /**
     * APIs related to User Personal Tabs (PersonalController)
     */

    public List<UserTabDTO.Header> getUserTabHeaders(String userId){
        List<User_Tab> userTabs = userTabRepository.findAllByUserTabCompositeKeyUserIdOrderByTabIdAsc(userId);

        List<UserTabDTO.Header> tabHeaders = new ArrayList<>(userTabs.size());

        for(User_Tab tab : userTabs){
            UserTabDTO.Header header = new UserTabDTO.Header(
                    tab.getUserTabCompositeKey().getTitle(),
                    tab.getTabId().toString());
            tabHeaders.add(header);
        }
        return tabHeaders;
    }

    public UserTabDTO.Response getUserTab(String userId, String tabId) throws BadRequestException {
        User_Tab userTab = userTabRepository.findByUserTabCompositeKeyUserIdAndTabId(userId, UUID.fromString(tabId))
                .orElseThrow(() -> new BadRequestException("Requested user tab could not be found"));

        return modelMapper.map(userTab, UserTabDTO.Response.class);
    }
}
