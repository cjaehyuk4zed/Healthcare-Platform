package platform.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.domain.keys.UserIpCompositeKey;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "user_ip")
public class User_Ip {

    @EmbeddedId
    private UserIpCompositeKey userIpCompositeKey;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "username", insertable = false, updatable = false)
    private User_Auth userAuth;
}
