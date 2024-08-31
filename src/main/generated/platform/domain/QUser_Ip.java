package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Ip is a Querydsl query type for User_Ip
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Ip extends EntityPathBase<User_Ip> {

    private static final long serialVersionUID = 2112339404L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Ip user_Ip = new QUser_Ip("user_Ip");

    public final QUser_Auth userAuth;

    public final platform.domain.keys.QUserIpCompositeKey userIpCompositeKey;

    public QUser_Ip(String variable) {
        this(User_Ip.class, forVariable(variable), INITS);
    }

    public QUser_Ip(Path<? extends User_Ip> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Ip(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Ip(PathMetadata metadata, PathInits inits) {
        this(User_Ip.class, metadata, inits);
    }

    public QUser_Ip(Class<? extends User_Ip> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userAuth = inits.isInitialized("userAuth") ? new QUser_Auth(forProperty("userAuth"), inits.get("userAuth")) : null;
        this.userIpCompositeKey = inits.isInitialized("userIpCompositeKey") ? new platform.domain.keys.QUserIpCompositeKey(forProperty("userIpCompositeKey")) : null;
    }

}

