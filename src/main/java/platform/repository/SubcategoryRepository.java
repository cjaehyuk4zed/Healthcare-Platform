package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.domain.Category;
import platform.domain.Subcategory;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, String> {

    List<Subcategory> findAllByCategory(String category);
}
