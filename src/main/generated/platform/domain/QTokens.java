package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTokens is a Querydsl query type for Tokens
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTokens extends EntityPathBase<Tokens> {

    private static final long serialVersionUID = -1210942487L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTokens tokens = new QTokens("tokens");

    public final StringPath clientIp = createString("clientIp");

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final StringPath token = createString("token");

    public final EnumPath<platform.auth.TokenTypes> tokenType = createEnum("tokenType", platform.auth.TokenTypes.class);

    public final QUser_Auth userAuth;

    public QTokens(String variable) {
        this(Tokens.class, forVariable(variable), INITS);
    }

    public QTokens(Path<? extends Tokens> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTokens(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTokens(PathMetadata metadata, PathInits inits) {
        this(Tokens.class, metadata, inits);
    }

    public QTokens(Class<? extends Tokens> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userAuth = inits.isInitialized("userAuth") ? new QUser_Auth(forProperty("userAuth"), inits.get("userAuth")) : null;
    }

}

