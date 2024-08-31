package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Tab is a Querydsl query type for User_Tab
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Tab extends EntityPathBase<User_Tab> {

    private static final long serialVersionUID = 1058022288L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Tab user_Tab = new QUser_Tab("user_Tab");

    public final StringPath tabContent = createString("tabContent");

    public final ComparablePath<java.util.UUID> tabId = createComparable("tabId", java.util.UUID.class);

    public final NumberPath<Integer> tabIndex = createNumber("tabIndex", Integer.class);

    public final DatePath<java.time.LocalDate> timestamp = createDate("timestamp", java.time.LocalDate.class);

    public final QUser_Info userInfoFK;

    public final platform.domain.keys.QUserTabCompositeKey userTabCompositeKey;

    public QUser_Tab(String variable) {
        this(User_Tab.class, forVariable(variable), INITS);
    }

    public QUser_Tab(Path<? extends User_Tab> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Tab(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Tab(PathMetadata metadata, PathInits inits) {
        this(User_Tab.class, metadata, inits);
    }

    public QUser_Tab(Class<? extends User_Tab> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userInfoFK = inits.isInitialized("userInfoFK") ? new QUser_Info(forProperty("userInfoFK"), inits.get("userInfoFK")) : null;
        this.userTabCompositeKey = inits.isInitialized("userTabCompositeKey") ? new platform.domain.keys.QUserTabCompositeKey(forProperty("userTabCompositeKey")) : null;
    }

}

