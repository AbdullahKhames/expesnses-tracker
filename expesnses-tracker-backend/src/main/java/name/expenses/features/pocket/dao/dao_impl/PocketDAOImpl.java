package name.expenses.features.pocket.dao.dao_impl;

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
import name.expenses.features.account.models.Account;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.dao.PocketDAO;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.*;
import java.util.stream.Collectors;

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
    public Page<Pocket> findAllWithoutAccount(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        try {
            TypedQuery<Pocket> typedQuery = entityManager.createQuery("SELECT p FROM Pocket p WHERE p.account is null", Pocket.class);
            List<Pocket> pockets = typedQuery.getResultList();
            return PageUtil.createPage(pageNumber, pageSize, pockets, pockets.size());

        } catch (Exception exception) {
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

    @Override
    public Set<Pocket> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }

        TypedQuery<Pocket> query = entityManager.createQuery(
                "SELECT a FROM Pocket a WHERE a.refNo IN :refNos", Pocket.class);
        query.setParameter("refNos", refNos);
        return new HashSet<>(query.getResultList());
    }

    @Override
    public Set<Pocket> saveAll(Set<Pocket> pockets) {
        Set<Pocket> savedPockets = new HashSet<>();
        if (pockets != null && !pockets.isEmpty()) {
            for (Pocket pocket : pockets) {
                if (pocket != null && pocket.getId() != null) {
                    entityManager.persist(pocket);
                    savedPockets.add(pocket);
                }
            }
//            entityManager.flush();
//            entityManager.clear();
        }
        return savedPockets;
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
    public List<Pocket> getByName(String name) {
        try {
            TypedQuery<Pocket> categoryTypedQuery = entityManager.createQuery("SELECT e from Pocket e WHERE e.name like :name", Pocket.class);
            categoryTypedQuery.setParameter("name", "%" + name + "%");
            return categoryTypedQuery.getResultList();
        }catch (NoResultException ex){
            return Collections.emptyList();
        }catch (NonUniqueResultException ex){
            throw new GeneralFailureException(GeneralFailureException.NON_UNIQUE_IDENTIFIER,
                    Map.of("error", String.format("the query didn't return a single result for reference number %s", name)));
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                    Map.of("original error message", ex.getMessage(),
                            "error", String.format("there was an error with your request couldn't find object with reference number %s", name)));
        }
    }

    @Override
    public Set<Pocket> updateAll(Set<Pocket> pockets) {
        return pockets
                .stream()
                .map(this::update)
                .collect(Collectors.toSet());
    }
}