package name.expenses.utils.category_association_manager;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.category.models.Category;
import name.expenses.features.sub_category.service.SubCategoryService;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CategoryAssociationManager {
    private final SubCategoryService subCategoryService;


    private final Map<CollectionAssociation, CategoryCollectionAdder> adderHandler = new HashMap<>(5);
    private final Map<CollectionAssociation, CategoryCollectionRemover> removerHandler = new HashMap<>(5);

    @PostConstruct
    private void init(){
        adderHandler.put(CollectionAssociation.SUB_CATEGORY, subCategoryService);


        removerHandler.put(CollectionAssociation.SUB_CATEGORY, subCategoryService);


    }
    public boolean addAssociation(Category category,
                                  CollectionAssociation CategoryCollectionAssociation,
                                  String refNo) {

        CategoryCollectionAdder CategoryCollectionAdder = adderHandler.get(CategoryCollectionAssociation);
        if (CategoryCollectionAdder == null) {
            return false;
        }
        return CategoryCollectionAdder.addAssociation(category, refNo);
    }


    public boolean removeAssociation(Category category,
                                     CollectionAssociation CategoryCollectionAssociation,
                                     String refNo) {

        CategoryCollectionRemover CategoryCollectionRemover = removerHandler.get(CategoryCollectionAssociation);
        if (CategoryCollectionRemover == null) {
            return false;
        }
        return CategoryCollectionRemover.removeAssociation(category, refNo);
    }
}