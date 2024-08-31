package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Fav is a Querydsl query type for User_Fav
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Fav extends EntityPathBase<User_Fav> {

    private static final long serialVersionUID = 1058008854L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Fav user_Fav = new QUser_Fav("user_Fav");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath postingFaved = createBoolean("postingFaved");

    public final ComparablePath<java.util.UUID> postingId = createComparable("postingId", java.util.UUID.class);

    public final StringPath userId = createString("userId");

    public final QUser_Info userInfo;

    public QUser_Fav(String variable) {
        this(User_Fav.class, forVariable(variable), INITS);
    }

    public QUser_Fav(Path<? extends User_Fav> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Fav(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Fav(PathMetadata metadata, PathInits inits) {
        this(User_Fav.class, metadata, inits);
    }

    public QUser_Fav(Class<? extends User_Fav> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

