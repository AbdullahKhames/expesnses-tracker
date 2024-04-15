package name.expenses.features.association;

import name.expenses.features.account.models.Account;
import name.expenses.features.base.models.BaseModel;
import name.expenses.features.category.models.Category;
import name.expenses.globals.responses.ResponseDto;

import java.util.List;
import java.util.Set;

public interface CollectionAdder<T> {
    boolean addAssociation(T entity, Models entityModel, String refNo);
    ResponseDto addAssociation(Object entity, Models entityModel, Set<String> refNos);
//    ResponseDto addAssociation(T entity, Models entityModel, Set<J> associationsUpdateDto);

    ResponseDto addDtoAssociation(Object entity, Models entityModel, Set<?> associationUpdateDto);
}
