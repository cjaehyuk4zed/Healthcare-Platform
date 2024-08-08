package platform.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import platform.domain.Category;
import platform.domain.Subcategory;
import platform.repository.CategoryRepository;
import platform.repository.SubcategoryRepository;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AdminService {

    CategoryRepository categoryRepository;
    SubcategoryRepository subcategoryRepository;

    public void addNewCategory(String categoryName){
        Category category = new Category(categoryName);
        categoryRepository.save(category);
        log.info("Added new category : {}", categoryName);
    }

    public void addNewSubcategory(String categoryName, String subcategoryName){
        Subcategory subcategory = new Subcategory(categoryName, subcategoryName);
        subcategoryRepository.save(subcategory);
        log.info("Added new subcategory : {} | within category : {} ", subcategoryName, categoryName);
    }
}
