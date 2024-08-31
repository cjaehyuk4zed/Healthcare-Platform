package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Auth is a Querydsl query type for User_Auth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Auth extends EntityPathBase<User_Auth> {

    private static final long serialVersionUID = -1561593587L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Auth user_Auth = new QUser_Auth("user_Auth");

    public final BooleanPath accountNonExpired = createBoolean("accountNonExpired");

    public final BooleanPath accountNonLocked = createBoolean("accountNonLocked");

    public final BooleanPath credentialsNonLocked = createBoolean("credentialsNonLocked");

    public final BooleanPath enabled = createBoolean("enabled");

    public final StringPath password = createString("password");

    public final EnumPath<platform.auth.Role> role = createEnum("role", platform.auth.Role.class);

    public final ListPath<Tokens, QTokens> tokens = this.<Tokens, QTokens>createList("tokens", Tokens.class, QTokens.class, PathInits.DIRECT2);

    public final QUser_Info userInfo;

    public final ListPath<User_Ip, QUser_Ip> userIp = this.<User_Ip, QUser_Ip>createList("userIp", User_Ip.class, QUser_Ip.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public QUser_Auth(String variable) {
        this(User_Auth.class, forVariable(variable), INITS);
    }

    public QUser_Auth(Path<? extends User_Auth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Auth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Auth(PathMetadata metadata, PathInits inits) {
        this(User_Auth.class, metadata, inits);
    }

    public QUser_Auth(Class<? extends User_Auth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

