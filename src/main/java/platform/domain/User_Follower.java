package platform.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User_Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "varchar(63) not null")
    private String userId;

    @Column(name = "follower_id", columnDefinition = "varchar(63) not null")
    private String followerId;

    @Builder
    public User_Follower(String userId, String followerId){
        this.userId = userId;
        this.followerId = followerId;
    }
}
