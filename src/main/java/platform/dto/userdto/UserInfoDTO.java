package platform.dto.userdto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static platform.constants.DirectoryMapConstants.PLATFORM_SERVER_SOCKET_ADDR;
import static platform.constants.DirectoryMapConstants.USER_CONTROLLER;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    // User profile pic and qr code aren't saved in MySql. They're retrieved by UserID
    @Schema(name = "userPic", type = "Resource", example = PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/users/{userId}/profile-pic")
    private String userPic;

    @Schema(name = "qrCode", type = "Resource", example = PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/users/{userId}/qr-code")
    private String qrCode;

    @Schema(example = "testuser")
    private String userId;

    @Schema(example = "Bob")
    private String firstName;

    @Schema(example = "Ross")
    private String lastName;

    @Schema(example = "testuser@email.com")
    private String email;

    @Schema(example = "+821011112222")
    private String phone;

    @Schema(example = "25")
    private int age;

    // to specify type as char and example as "M" because SwaggerUI recognizes char as String
    @Schema(name = "gender", type = "char", example = "M")
    private char gender;

    @Schema(example = "South Korea")
    private String country;

    @Schema(example = "Suwon")
    private String city;

    @Schema(example = "도로명 101동 101호")
    private String address;

    @Schema(example = "2024-02-24")
    private LocalDate timestamp;

    @Schema(example = "2024-01-02")
    private LocalDate dateRegistered;

    @Schema(example = "2000-01-01")
    private LocalDate dateOfBirth;

    private int followerCount;

    private int followingCount;

    private int postCount;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Basic{
        // User profile pic and qr code aren't saved in MySql. They're retrieved by UserID
        @Schema(name = "userPic", type = "Resource", example = PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/users/{userId}/profile-pic")
        private String userPic;

        @Schema(name = "qrCode", type = "Resource", example = PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/users/{userId}/qr-code")
        private String qrCode;

        @Schema(example = "testuser")
        private String userId;

        @Schema(example = "Bob")
        private String firstName;

        @Schema(example = "Ross")
        private String lastName;

        @Schema(example = "Suwon")
        private String city;

        // to specify type as char and example as "M" because SwaggerUI recognizes char as String
        @Schema(name = "gender", type = "char", example = "M")
        private char gender;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Save{
        @Schema(name = "userPic", type = "Resource", example = PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/users/{userId}/profile-pic")
        private String userPic;

        @Schema(example = "testuser")
        private String userId;

        @Schema(example = "Bob")
        private String firstName;

        @Schema(example = "Ross")
        private String lastName;

        @Schema(example = "testuser@email.com")
        private String email;

        @Schema(example = "+821011112222")
        private String phone;

        @Schema(example = "25")
        private int age;

        // to specify type as char and example as "M" because SwaggerUI recognizes char as String
        @Schema(name = "gender", type = "char", example = "M")
        private char gender;

        @Schema(example = "South Korea")
        private String country;

        @Schema(example = "Suwon")
        private String city;

        @Schema(example = "도로명 101동 101호")
        private String address;

        @Schema(example = "2000-01-01")
        private LocalDate dateOfBirth;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @Schema(name = "userPic", type = "Resource", example = PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/users/{userId}/profile-pic")
        private String userPic;

        @Schema(name = "qrCode", type = "Resource", example = PLATFORM_SERVER_SOCKET_ADDR + USER_CONTROLLER + "/users/{userId}/qr-code")
        private String qrCode;

        @Schema(example = "testuser")
        private String userId;

        @Schema(example = "Bob")
        private String firstName;

        @Schema(example = "Ross")
        private String lastName;

        @Schema(example = "testuser@email.com")
        private String email;

        @Schema(example = "+821011112222")
        private String phone;

        @Schema(example = "25")
        private int age;

        // to specify type as char and example as "M" because SwaggerUI recognizes char as String
        @Schema(name = "gender", type = "char", example = "M")
        private char gender;

        @Schema(example = "South Korea")
        private String country;

        @Schema(example = "Suwon")
        private String city;

        @Schema(example = "도로명 101동 101호")
        private String address;

        @Schema(example = "2024-02-24")
        private String timestamp;

        @Schema(example = "2024-01-02")
        private String dateRegistered;

        @Schema(example = "2000-01-01")
        private String dateOfBirth;

        private int followerCount;

        private int followingCount;

        private int postCount;

        public void formatTimestamp(LocalDate time){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.timestamp = time.format(formatter);
        }

        public void formatDateRegistered(LocalDate time){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.dateRegistered = time.format(formatter);
        }

        public void formatDateOfBirth(LocalDate time){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.dateOfBirth = time.format(formatter);
        }
    }

}
