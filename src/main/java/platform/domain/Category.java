package platform.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @Column(name = "category", columnDefinition = "VARCHAR(63) not null")
    private String category;

    @OneToMany(mappedBy = "categoryFK", cascade = CascadeType.ALL)
    private List<Subcategory> subcategory;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<User_Interest> userInterests;

    @OneToMany(mappedBy = "categoryFK", cascade = CascadeType.ALL)
    private List<Posting_Info> postInfos;


    public Category(String categoryName) {
        this.category = categoryName;
    }
}
