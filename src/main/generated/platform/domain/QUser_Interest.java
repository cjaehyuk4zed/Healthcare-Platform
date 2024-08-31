package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Interest is a Querydsl query type for User_Interest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Interest extends EntityPathBase<User_Interest> {

    private static final long serialVersionUID = -1211660273L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Interest user_Interest = new QUser_Interest("user_Interest");

    public final QCategory category;

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    public final QSubcategory subcategory;

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final QUser_Info userInfo;

    public final platform.domain.keys.QUserInterestCompositeKey userInterestCompositeKey;

    public final BooleanPath userInterested = createBoolean("userInterested");

    public QUser_Interest(String variable) {
        this(User_Interest.class, forVariable(variable), INITS);
    }

    public QUser_Interest(Path<? extends User_Interest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Interest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Interest(PathMetadata metadata, PathInits inits) {
        this(User_Interest.class, metadata, inits);
    }

    public QUser_Interest(Class<? extends User_Interest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.subcategory = inits.isInitialized("subcategory") ? new QSubcategory(forProperty("subcategory"), inits.get("subcategory")) : null;
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
        this.userInterestCompositeKey = inits.isInitialized("userInterestCompositeKey") ? new platform.domain.keys.QUserInterestCompositeKey(forProperty("userInterestCompositeKey")) : null;
    }

}

