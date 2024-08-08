package platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Posting_Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "VARCHAR(63) not null")
    private String userId;

    @Column(name = "posting_id", columnDefinition = "BINARY(16) not NULL")
    private UUID postingId;

    @Column(name = "posting_creator_id", columnDefinition = "VARCHAR(63) not null")
    private String postingCreatorId;

    @Column(name = "posting_reported", columnDefinition = "BOOLEAN default FALSE")
    private boolean postingReported;

    @Column(name = "timestamp", columnDefinition = "DATETIME")
    private LocalDateTime timestamp;


    @ManyToOne(targetEntity = User_Info.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false, nullable = false)
    private User_Info userInfo;

    @Builder
    public Posting_Report(String userId, UUID postingId, String postingCreatorId, boolean postingReported){
        this.userId = userId;
        this.postingId = postingId;
        this.postingCreatorId = postingCreatorId;
        this.postingReported = postingReported;
        this.timestamp = LocalDateTime.now();
    }
}
