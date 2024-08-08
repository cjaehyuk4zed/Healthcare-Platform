package platform.dto;


import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Setter
@AllArgsConstructor
public class PersonalDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class TopInfo {
        String profilePic;
        String qrCode;
        String userId;
        String firstName;
        String lastName;
        String email;
        String phone;
        boolean isPrincipal;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class LeftInfo{
        String dateRegistered;
        String timestamp;
        int postingCount;
        int followingCount;
        int followerCount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class CenterInfo{

        // 모든 게시글...?

    }
}
