package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFile_Info is a Querydsl query type for File_Info
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFile_Info extends EntityPathBase<File_Info> {

    private static final long serialVersionUID = 1077796098L;

    public static final QFile_Info file_Info = new QFile_Info("file_Info");

    public final StringPath file_name = createString("file_name");

    public final StringPath file_path = createString("file_path");

    public QFile_Info(String variable) {
        super(File_Info.class, forVariable(variable));
    }

    public QFile_Info(Path<? extends File_Info> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFile_Info(PathMetadata metadata) {
        super(File_Info.class, metadata);
    }

}

