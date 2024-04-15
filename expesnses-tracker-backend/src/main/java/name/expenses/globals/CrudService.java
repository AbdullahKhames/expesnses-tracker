package name.expenses.globals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import name.expenses.globals.responses.ResponseDto;

import java.util.Optional;
import java.util.Set;

public interface CrudService <T, J, H, E> {

    default E update(E entity, EntityManager entityManager){
        return entityManager.merge(entity);
    }
    ResponseDto create(T entityReqDto);
    ResponseDto get(H refNo);
    Optional<E> getEntity(H refNo);

    ResponseDto update(H refNo, J entityUpdateDto);
    ResponseDto delete(H refNo);
    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    Set<E> getEntities(Set<H> refNos);
}
