package platform.domain.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserIpCompositeKey implements Serializable {

    @Column(name = "user_id", columnDefinition = "VARCHAR(63) not null")
    private String userId;

    @Column(name = "ip_addr", columnDefinition = "VARBINARY(16) not null")
    private String ipAddr;
}
