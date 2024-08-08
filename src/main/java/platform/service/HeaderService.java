package platform.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import platform.domain.Category;
import platform.domain.Subcategory;
import platform.repository.CategoryRepository;
import platform.repository.SubcategoryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class HeaderService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    public List<List<String>> getAllCategories(){
        List<List<String>> listList = new ArrayList<>();

        // 상위 카테고리 목록 받기 (Category 객체로 받게 된다)
        List<Category> categories = categoryRepository.findAll();

        // 상위 카테고리 알파벳순 정렬
        List<String> categoryList = new ArrayList<>();
        for(Category c : categories){
            categoryList.add(c.getCategory());
        }
        Collections.sort(categoryList, Comparator.naturalOrder());

        // 하위 카테고리 탐색 후 List<List<>>에 추가. 첫 원소는 상위 카테고리, 그 아래에 하위 카테고리들
        for(String c : categoryList){
            List<String> list = new ArrayList<>();
            list.add(c);

            // 하위 카테고리들 알파벳순 정렬 후 상위 카테고리 아래에 추가
            List<String> subList = getAllSubcategories(c);
            for(String s : subList){list.add(s);}
            // 최종 List<List<>>에 추가
            listList.add(list);
        }

        return listList;
    }

    public List<String> getAllSubcategories(String category){
        List<Subcategory> subcategoryList = subcategoryRepository.findAllByCategory(category);
        if(subcategoryList.isEmpty()){
            throw new IllegalArgumentException("This category does not exist");
        }

        List<String> list = new ArrayList<>();
        for(Subcategory s : subcategoryList){list.add(s.getSubcategory());}

        Collections.sort(list, Comparator.naturalOrder());
        return list;
    }
}
