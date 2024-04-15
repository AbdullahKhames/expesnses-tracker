package name.expenses.features.association;

import name.expenses.globals.responses.ResponseDto;

import java.util.Set;

public interface CollectionRemover  <T> {
    boolean removeAssociation(T entity, Models entityModel, String refNo);
    ResponseDto removeAssociation(Object entity, Models entityModel, Set<String> refNo);
    ResponseDto removeDtoAssociation(Object entity, Models entityModel, Set<?> associationsUpdateDto);

}