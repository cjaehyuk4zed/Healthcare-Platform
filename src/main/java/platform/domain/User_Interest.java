package platform.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;
import platform.domain.keys.UserInterestCompositeKey;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User_Interest {

    @EmbeddedId
    private UserInterestCompositeKey userInterestCompositeKey;

    // null 일때는, likes가 일정 수치에 도달하면 "관심카테고리 추가하겠습니까?" 알림 전송
    // true이면 추천 알고리즘에 사용
    // false이면 더 이상 관심카테고리에 추가 알림 전송 안함
    @Nullable
    @Column(name = "user_interested", columnDefinition = "boolean default null")
    private boolean userInterested;

    @Column(name = "likes", columnDefinition = "INT default 1")
    private int likes;

    @Column(name = "timestamp", columnDefinition = "DATETIME")
    private LocalDateTime timestamp;

    @Builder
    public User_Interest(UserInterestCompositeKey compositeKey, Boolean userInterested, int likes){
        this.userInterestCompositeKey = compositeKey;
        this.userInterested = userInterested;
        this.likes = likes;
        this.timestamp = LocalDateTime.now();
    }

    @MapsId("user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", columnDefinition = "VARCHAR(63) not null")
    private User_Info userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", referencedColumnName = "category", columnDefinition = "VARCHAR(63) not null", insertable = false, updatable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory", referencedColumnName = "subcategory", columnDefinition = "VARCHAR(63) not null", insertable = false, updatable = false)
    private Subcategory subcategory;

}
