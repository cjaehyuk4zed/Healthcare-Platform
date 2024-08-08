package platform.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request_Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "VARCHAR(63) not null")
    private String userId;

    @Column(name = "request_log")
    private String requestLog;

    @Column(name = "timestamp", columnDefinition = "DATETIME")
    private LocalDateTime timestamp;

    @Builder
    public Request_Log(String userId, String requestLog){
        this.userId = userId;
        this.requestLog = requestLog;
        this.timestamp = LocalDateTime.now();
    }

}
