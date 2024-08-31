package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubcategory is a Querydsl query type for Subcategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubcategory extends EntityPathBase<Subcategory> {

    private static final long serialVersionUID = 1133043279L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubcategory subcategory1 = new QSubcategory("subcategory1");

    public final StringPath category = createString("category");

    public final QCategory categoryFK;

    public final ListPath<Posting_Info, QPosting_Info> postInfos = this.<Posting_Info, QPosting_Info>createList("postInfos", Posting_Info.class, QPosting_Info.class, PathInits.DIRECT2);

    public final StringPath subcategory = createString("subcategory");

    public final ListPath<User_Interest, QUser_Interest> userInterests = this.<User_Interest, QUser_Interest>createList("userInterests", User_Interest.class, QUser_Interest.class, PathInits.DIRECT2);

    public QSubcategory(String variable) {
        this(Subcategory.class, forVariable(variable), INITS);
    }

    public QSubcategory(Path<? extends Subcategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubcategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubcategory(PathMetadata metadata, PathInits inits) {
        this(Subcategory.class, metadata, inits);
    }

    public QSubcategory(Class<? extends Subcategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categoryFK = inits.isInitialized("categoryFK") ? new QCategory(forProperty("categoryFK")) : null;
    }

}

