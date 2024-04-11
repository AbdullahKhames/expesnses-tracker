package name.expenses.utils.category_association_manager;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.models.Category;
import name.expenses.features.sub_category.service.SubCategoryService;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class UpdateCategoryServiceImpl {
    private final SubCategoryService subCategoryService;


    private final List<UpdateCategoryAssociation> updateProductAssociations = new ArrayList<>(4);
    @PostConstruct
    private void init(){
        updateProductAssociations.add(subCategoryService);

    }
    public void updateCategoryAssociations(Category category, CategoryUpdateDto categoryUpdateDto){
        updateProductAssociations
                .forEach(updateProductAssociation ->
                        updateProductAssociation.updateAssociation(category, categoryUpdateDto));
    }

}