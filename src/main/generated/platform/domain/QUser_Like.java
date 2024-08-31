package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Like is a Querydsl query type for User_Like
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Like extends EntityPathBase<User_Like> {

    private static final long serialVersionUID = -1561277700L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Like user_Like = new QUser_Like("user_Like");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ComparablePath<java.util.UUID> postingId = createComparable("postingId", java.util.UUID.class);

    public final BooleanPath postingLiked = createBoolean("postingLiked");

    public final StringPath userId = createString("userId");

    public final QUser_Info userInfo;

    public QUser_Like(String variable) {
        this(User_Like.class, forVariable(variable), INITS);
    }

    public QUser_Like(Path<? extends User_Like> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Like(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Like(PathMetadata metadata, PathInits inits) {
        this(User_Like.class, metadata, inits);
    }

    public QUser_Like(Class<? extends User_Like> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

