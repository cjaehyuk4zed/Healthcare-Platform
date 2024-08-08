package platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User_Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "VARCHAR(63) not NULL")
    private String userId;

    @Column(name = "posting_id", columnDefinition = "BINARY(16) not NULL")
    private UUID postingId;

    @Column(name = "posting_liked", columnDefinition = "BOOLEAN default FALSE")
    private boolean postingLiked;

    @ManyToOne(targetEntity = User_Info.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false, nullable = false)
    private User_Info userInfo;

    @Builder
    public User_Like(String userId, UUID postingId, boolean postingLiked){
        this.userId = userId;
        this.postingId = postingId;
        this.postingLiked = postingLiked;
    }
}