package platform.domain;

import jakarta.persistence.*;
import lombok.*;
import platform.auth.TokenTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Tokens {
    @Id
    @Column(name = "token")
    private String token;

    @Column(name = "token_type")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    public TokenTypes tokenType = TokenTypes.BEARER;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "username", columnDefinition = "VARCHAR(63)")
    private User_Auth userAuth;

    @Column(name = "client_ip", columnDefinition = "VARBINARY(16) not null")
    private String clientIp;
}
