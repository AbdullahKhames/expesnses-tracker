package name.expenses.features.pocket.dao.dao_impl;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import name.expenses.config.filters.RepoAdvice;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.features.account.models.Account;
import name.expenses.features.pocket.dao.PocketDAO;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
@Interceptors(RepoAdvice.class)
@Transactional
public class PocketDAOImpl implements PocketDAO {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public Pocket create(Pocket pocket) {
        try{
            if (pocket.getId() != null && entityManager.find(Pocket.class, pocket.getId()) != null) {
                return entityManager.merge(pocket);
            } else {
                entityManager.persist(pocket);
                return pocket;
            }
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_PERSISTING,
                    Map.of("original error message", ex.getMessage(),
                            "error", "there was an error with your request couldn't persist"));
        }
    }

    @Override
    public Optional<Pocket> get(String refNo) {
        try {
            TypedQuery<Pocket> pocketTypedQuery = entityManager.createQuery("SELECT e from Pocket e WHERE e.refNo = :refNo", Pocket.class);
            pocketTypedQuery.setParameter("refNo", refNo);
            return Optional.ofNullable(pocketTypedQuery.getSingleResult());
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
    public Page<Pocket> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pocket> query = cb.createQuery(Pocket.class);
        Root<Pocket> root = query.from(Pocket.class);

        query.select(root);

        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, Pocket.class)) {
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
            TypedQuery<Pocket> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) ((pageNumber - 1) * pageSize));
            typedQuery.setMaxResults(Math.toIntExact(pageSize));
            List<Pocket> pockets = typedQuery.getResultList();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(Pocket.class)));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
            return PageUtil.createPage(pageNumber, pageSize, pockets, totalElements);

        }catch (Exception exception){
            throw new GeneralFailureException(GeneralFailureException.ERROR_FETCH,
                    Map.of("original message", exception.getMessage().substring(0, 15),
                            "error", "there was an error with your request"));
        }

    }

    @Override
    public List<Pocket> get() {
        return entityManager.createQuery("SELECT e FROM Pocket e", Pocket.class).getResultList();
    }

    @Override
    public Pocket update(Pocket pocket) {
        try {
            entityManager.getTransaction().begin();
            Pocket updatedPocket = entityManager.merge(pocket);
            entityManager.getTransaction().commit();
            return updatedPocket;

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
            TypedQuery<Pocket> pocketTypedQuery = entityManager.createQuery("SELECT e from Pocket e WHERE e.refNo = :refNo", Pocket.class);
            pocketTypedQuery.setParameter("refNo", refNo);
            Pocket pocket = pocketTypedQuery.getSingleResult();
            if (pocket != null) {
                entityManager.remove(pocket);
            }
            return refNo;
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_DELETE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't delete entity"));
        }
    }

    @Override
    public Long checkAccountAssociation(Pocket pocket) {
        Account account;
        try{
            TypedQuery<Account> query = entityManager.createQuery("SELECT c FROM Account c JOIN c.pockets s WHERE s.id = :id", Account.class);
            query.setParameter("id", pocket.getId());
            account = query.getSingleResult();
        }catch (Exception ex){
            return null;
        }
        return account.getId();
    }
}