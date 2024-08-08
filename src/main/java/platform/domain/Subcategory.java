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
public class Subcategory {

    @Id
    @Column(name = "subcategory", columnDefinition = "VARCHAR(63) not null")
    private String subcategory;

    @Column(name = "category", columnDefinition = "VARCHAR(63) not null")
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", referencedColumnName = "category", insertable = false, updatable = false)
    private Category categoryFK;

    @OneToMany(mappedBy = "subcategory", cascade = CascadeType.ALL)
    private List<User_Interest> userInterests;

    @OneToMany(mappedBy = "subcategoryFK", cascade = CascadeType.ALL)
    private List<Posting_Info> postInfos;


    public Subcategory(String categoryName, String subcategoryName) {
        this.category = categoryName;
        this.subcategory = subcategoryName;
    }
}
