package platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
//@ToString(exclude = "userInfo")
public class User_Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "VARCHAR(63) not null")
    private String userId;

    @Column(name = "reported_user_id", columnDefinition = "VARCHAR(63) not null")
    private String reportedUserId;

    @Column(name = "user_reported", columnDefinition = "BOOLEAN default FALSE")
    private boolean userReported;

    @Column(name = "timestamp", columnDefinition = "DATETIME")
    private LocalDateTime timestamp;

    @ManyToOne(targetEntity = User_Info.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false, nullable = false)
    private User_Info userInfo;

    @ManyToOne(targetEntity = User_Info.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", referencedColumnName = "user_id", insertable = false, updatable = false, nullable = false)
    private User_Info reportedUserInfo;

    @Builder
    public User_Report(String userId, String reportedUserId, boolean userReported){
        this.userId = userId;
        this.reportedUserId = reportedUserId;
        this.userReported = userReported;
        this.timestamp = LocalDateTime.now();
    }

}
