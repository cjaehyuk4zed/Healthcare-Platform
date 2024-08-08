package platform.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static platform.constants.DirectoryMapConstants.*;

@Service
@AllArgsConstructor
@Slf4j
// Service file specifically created for keeping track of URI links, in order to avoid having to edit a huge bunch of stuff every time
public class URIService {

    public String setUserQrCodeURI(String userId){
        return PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/" + userId + "/qr-code";
    }

    public String setUserProfilePicURI(String userId){
        return PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/" + userId + "/profile-pic";
    }

    public String setImageURIByImageId(String imageId){
        return PLATFORM_SERVER_SOCKET_ADDR + IMAGE_CONTROLLER + "/" + imageId;
    }

    public String setImageURIByImageName(String imageId){
        return PLATFORM_SERVER_SOCKET_ADDR + IMAGE_CONTROLLER + "/" + imageId;
    }

}
