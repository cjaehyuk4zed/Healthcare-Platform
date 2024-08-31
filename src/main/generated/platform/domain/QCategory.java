package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = 1374706573L;

    public static final QCategory category1 = new QCategory("category1");

    public final StringPath category = createString("category");

    public final ListPath<Posting_Info, QPosting_Info> postInfos = this.<Posting_Info, QPosting_Info>createList("postInfos", Posting_Info.class, QPosting_Info.class, PathInits.DIRECT2);

    public final ListPath<Subcategory, QSubcategory> subcategory = this.<Subcategory, QSubcategory>createList("subcategory", Subcategory.class, QSubcategory.class, PathInits.DIRECT2);

    public final ListPath<User_Interest, QUser_Interest> userInterests = this.<User_Interest, QUser_Interest>createList("userInterests", User_Interest.class, QUser_Interest.class, PathInits.DIRECT2);

    public QCategory(String variable) {
        super(Category.class, forVariable(variable));
    }

    public QCategory(Path<? extends Category> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategory(PathMetadata metadata) {
        super(Category.class, metadata);
    }

}

