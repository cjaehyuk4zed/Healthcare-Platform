package platform.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "user_info")
//@Schema(description = "사용자 프로필 정보")
//@Data // Getter, Setter, toString, equals, hashCode
@Getter
@Setter
@AllArgsConstructor // Needed to prevent the "No default constructor" error
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class User_Info {
    @Id
    @Column(name = "user_id", unique = true, columnDefinition = "VARCHAR(63)")
    private String userId;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name", columnDefinition = "VARCHAR(63) not null")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "VARCHAR(63) not null")
    private String lastName;

    // varchar(15) to support all international formats
    // INT type cannot be used, as it deleted preceding '0' characters
    @Column(name = "phone", columnDefinition = "VARCHAR(15)")
    private String phone;

    @Column(name = "age")
    private Long age;

    @Column(name = "gender")
    private char gender;

    @Column(name = "country", columnDefinition = "VARCHAR(127)")
    private String country;

    @Column(name = "city", columnDefinition = "VARCHAR(127)")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "timestamp")
    private LocalDate timestamp;

    @Column(name = "dateRegistered")
    private LocalDate dateRegistered;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "follower_count", columnDefinition = "INT default 0")
    private Long followerCount;

    @Column(name = "following_count", columnDefinition = "INT default 0")
    private Long followingCount;

    @Column(name = "posting_count", columnDefinition = "INT default 0")
    private Long postingCount;


//    Foreign Key relations
//    Foreign Key relations
    @MapsId("userId")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "username")
    private User_Auth userAuth;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<Posting_Info> postInfo;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<Image> images;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<Posting_Attachment> postAttachments;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<User_Fav> userFaves;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<User_Like> userLikes;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<User_Report> userReports;

    @OneToMany(mappedBy = "reportedUserInfo", cascade = CascadeType.ALL)
    private List<User_Report> userReported;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<Posting_Report> postReports;

    @OneToMany(mappedBy = "userInfoFK", cascade = CascadeType.ALL)
    private List<User_Tab> userTabs;

    // User profile pic and qr code aren't included in the domain entity. They're retrieved by UserID
}
