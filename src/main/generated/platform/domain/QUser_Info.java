package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser_Info is a Querydsl query type for User_Info
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Info extends EntityPathBase<User_Info> {

    private static final long serialVersionUID = -1561362413L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser_Info user_Info = new QUser_Info("user_Info");

    public final StringPath address = createString("address");

    public final NumberPath<Long> age = createNumber("age", Long.class);

    public final StringPath city = createString("city");

    public final StringPath country = createString("country");

    public final DatePath<java.time.LocalDate> dateOfBirth = createDate("dateOfBirth", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dateRegistered = createDate("dateRegistered", java.time.LocalDate.class);

    public final StringPath email = createString("email");

    public final StringPath firstName = createString("firstName");

    public final NumberPath<Long> followerCount = createNumber("followerCount", Long.class);

    public final NumberPath<Long> followingCount = createNumber("followingCount", Long.class);

    public final ComparablePath<Character> gender = createComparable("gender", Character.class);

    public final ListPath<Image, QImage> images = this.<Image, QImage>createList("images", Image.class, QImage.class, PathInits.DIRECT2);

    public final StringPath lastName = createString("lastName");

    public final StringPath phone = createString("phone");

    public final ListPath<Posting_Attachment, QPosting_Attachment> postAttachments = this.<Posting_Attachment, QPosting_Attachment>createList("postAttachments", Posting_Attachment.class, QPosting_Attachment.class, PathInits.DIRECT2);

    public final ListPath<Posting_Info, QPosting_Info> postInfo = this.<Posting_Info, QPosting_Info>createList("postInfo", Posting_Info.class, QPosting_Info.class, PathInits.DIRECT2);

    public final NumberPath<Long> postingCount = createNumber("postingCount", Long.class);

    public final ListPath<Posting_Report, QPosting_Report> postReports = this.<Posting_Report, QPosting_Report>createList("postReports", Posting_Report.class, QPosting_Report.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> timestamp = createDate("timestamp", java.time.LocalDate.class);

    public final QUser_Auth userAuth;

    public final ListPath<User_Fav, QUser_Fav> userFaves = this.<User_Fav, QUser_Fav>createList("userFaves", User_Fav.class, QUser_Fav.class, PathInits.DIRECT2);

    public final StringPath userId = createString("userId");

    public final ListPath<User_Like, QUser_Like> userLikes = this.<User_Like, QUser_Like>createList("userLikes", User_Like.class, QUser_Like.class, PathInits.DIRECT2);

    public final ListPath<User_Report, QUser_Report> userReported = this.<User_Report, QUser_Report>createList("userReported", User_Report.class, QUser_Report.class, PathInits.DIRECT2);

    public final ListPath<User_Report, QUser_Report> userReports = this.<User_Report, QUser_Report>createList("userReports", User_Report.class, QUser_Report.class, PathInits.DIRECT2);

    public final ListPath<User_Tab, QUser_Tab> userTabs = this.<User_Tab, QUser_Tab>createList("userTabs", User_Tab.class, QUser_Tab.class, PathInits.DIRECT2);

    public QUser_Info(String variable) {
        this(User_Info.class, forVariable(variable), INITS);
    }

    public QUser_Info(Path<? extends User_Info> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser_Info(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser_Info(PathMetadata metadata, PathInits inits) {
        this(User_Info.class, metadata, inits);
    }

    public QUser_Info(Class<? extends User_Info> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userAuth = inits.isInitialized("userAuth") ? new QUser_Auth(forProperty("userAuth"), inits.get("userAuth")) : null;
    }

}

