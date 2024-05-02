package name.expenses.features.pocket_transfer.dao.dao_impl;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import name.expenses.config.advice.RepoAdvice;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.features.pocket_transfer.dao.PocketAmountDAO;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.*;

@Stateless
@Interceptors(RepoAdvice.class)
@Transactional
public class PocketAmountDAOImpl implements PocketAmountDAO {
    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public PocketAmount create(PocketAmount pocketAmount) {

        try{
            entityManager.getTransaction().begin();
            if (pocketAmount.getId() != null && entityManager.find(PocketAmount.class, pocketAmount.getId()) != null) {
                PocketAmount pocketAmount1 = entityManager.merge(pocketAmount);
                entityManager.getTransaction().commit();
                return pocketAmount1;
            } else {
                entityManager.persist(pocketAmount);
                entityManager.getTransaction().commit();
                return pocketAmount;
            }
        }catch (Exception ex){
            entityManager.getTransaction().rollback();
            throw new GeneralFailureException(GeneralFailureException.ERROR_PERSISTING,
                    Map.of("original error message", ex.getMessage(),
                            "error", "there was an error with your request couldn't persist"));
        }
    }

    @Override
    public Optional<PocketAmount> get(String refNo) {
        try {
            TypedQuery<PocketAmount> pocketAmountTypedQuery = entityManager.createQuery("SELECT e from PocketAmount e WHERE e.refNo = :refNo", PocketAmount.class);
            pocketAmountTypedQuery.setParameter("refNo", refNo);
            return Optional.ofNullable(pocketAmountTypedQuery.getSingleResult());
        }catch (NoResultException ex){
            return Optional.empty();
        }catch (NonUniqueResultException ex){
            throw new GeneralFailureException(GeneralFailureException.NON_UNIQUE_IDENTIFIER,
                    Map.of("error", String.format("the query didn't return a single result for reference number %s", refNo)));
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                    Map.of("original error message", ex.getMessage(),
                            "error", String.format("there was an error with your request couldn't find object with reference number %s", refNo)));
        }
    }

    @Override
    public Page<PocketAmount> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PocketAmount> query = cb.createQuery(PocketAmount.class);
        Root<PocketAmount> root = query.from(PocketAmount.class);

        query.select(root);

        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, PocketAmount.class)) {
            sortByPath = root.get(sortBy);
        }else {
            sortByPath = root.get("id");
        }

        if (sortDirection == SortDirection.ASC) {
            query.orderBy(cb.asc(sortByPath));
        } else {
            query.orderBy(cb.desc(sortByPath));
        }
        try {
            TypedQuery<PocketAmount> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) ((pageNumber - 1) * pageSize));
            typedQuery.setMaxResults(Math.toIntExact(pageSize));
            List<PocketAmount> pocketAmounts = typedQuery.getResultList();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(PocketAmount.class)));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
            return PageUtil.createPage(pageNumber, pageSize, pocketAmounts, totalElements);

        }catch (Exception exception){
            throw new GeneralFailureException(GeneralFailureException.ERROR_FETCH,
                    Map.of("original message", exception.getMessage().substring(0, 15),
                            "error", "there was an error with your request"));
        }

    }

    @Override
    public List<PocketAmount> get() {
        return entityManager.createQuery("SELECT e FROM PocketAmount e", PocketAmount.class).getResultList();
    }

    @Override
    public PocketAmount update(PocketAmount pocketAmount) {
        try {
            entityManager.getTransaction().begin();
            PocketAmount updatedPocketAmount = entityManager.merge(pocketAmount);
            entityManager.getTransaction().commit();
            return updatedPocketAmount;

        }catch (Exception ex){
            entityManager.getTransaction().rollback();
            throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't update entity"));
        }
    }

    @Override
    public String delete(String refNo) {
        try {
            TypedQuery<PocketAmount> pocketAmountTypedQuery = entityManager.createQuery("SELECT e from PocketAmount e WHERE e.refNo = :refNo", PocketAmount.class);
            pocketAmountTypedQuery.setParameter("refNo", refNo);
            PocketAmount pocketAmount = pocketAmountTypedQuery.getSingleResult();
            if (pocketAmount != null) {
                entityManager.remove(pocketAmount);
            }
            return refNo;
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_DELETE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't delete entity"));
        }
    }

    @Override
    public Set<PocketAmount> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }

        TypedQuery<PocketAmount> query = entityManager.createQuery(
                "SELECT a FROM PocketAmount a WHERE a.refNo IN :refNos", PocketAmount.class);
        query.setParameter("refNos", refNos);
        return new HashSet<>(query.getResultList());
    }
}
