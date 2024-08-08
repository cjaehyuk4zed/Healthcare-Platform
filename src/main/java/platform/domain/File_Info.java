package platform.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "html_files_test")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "file_name"))
public class File_Info {
    @Id
    String file_name;

    @Column
    String file_path;


}
