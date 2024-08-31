package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Report is a Querydsl query type for User_Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Report extends EntityPathBase<User_Report> {

    private static final long serialVersionUID = -1276040359L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Report user_Report = new QUser_Report("user_Report");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath reportedUserId = createString("reportedUserId");

    public final QUser_Info reportedUserInfo;

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public final StringPath userId = createString("userId");

    public final QUser_Info userInfo;

    public final BooleanPath userReported = createBoolean("userReported");

    public QUser_Report(String variable) {
        this(User_Report.class, forVariable(variable), INITS);
    }

    public QUser_Report(Path<? extends User_Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Report(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Report(PathMetadata metadata, PathInits inits) {
        this(User_Report.class, metadata, inits);
    }

    public QUser_Report(Class<? extends User_Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reportedUserInfo = inits.isInitialized("reportedUserInfo") ? new QUser_Info(forProperty("reportedUserInfo"), inits.get("reportedUserInfo")) : null;
        this.userInfo = inits.isInitialized("userInfo") ? new QUser_Info(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

