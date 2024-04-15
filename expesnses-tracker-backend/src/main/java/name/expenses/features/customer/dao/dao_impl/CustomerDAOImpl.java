package name.expenses.features.customer.dao.dao_impl;

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
import name.expenses.features.customer.dao.CustomerDAO;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.*;

@Stateless
@Interceptors(RepoAdvice.class)
@Transactional
public class CustomerDAOImpl implements CustomerDAO {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public Customer create(Customer customer) {
        try{
            if (customer.getUser().getId() != null && entityManager.find(Customer.class, customer.getUser().getId()) != null) {
                return entityManager.merge(customer);
            } else {
                entityManager.persist(customer);
                return customer;
            }
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_PERSISTING,
                    Map.of("original error message", ex.getMessage(),
                            "error", "there was an error with your request couldn't persist"));
        }
    }

    @Override
    public Optional<Customer> get(String refNo) {
        try {
            TypedQuery<Customer> customerTypedQuery = entityManager.createQuery("SELECT e from Customer e WHERE e.user.refNo = :refNo", Customer.class);
            customerTypedQuery.setParameter("refNo", refNo);
            return Optional.ofNullable(customerTypedQuery.getSingleResult());
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
    public Optional<Customer> get(Long id) {
        try {
            TypedQuery<Customer> customerTypedQuery = entityManager.createQuery("SELECT e from Customer e WHERE e.id = :id", Customer.class);
            customerTypedQuery.setParameter("id", id);
            return Optional.ofNullable(customerTypedQuery.getSingleResult());
        }catch (NoResultException ex){
            return Optional.empty();
        }catch (NonUniqueResultException ex){
            throw new GeneralFailureException(GeneralFailureException.NON_UNIQUE_IDENTIFIER,
                    Map.of("error", String.format("the query didn't return a single result for reference number %s", id)));
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                    Map.of("original error message", ex.getMessage(),
                            "error", String.format("there was an error with your request couldn't find object with reference number %s", id)));
        }
    }

    @Override
    public Page<Customer> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);

        query.select(root);

        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, Customer.class)) {
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
            TypedQuery<Customer> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) ((pageNumber - 1) * pageSize));
            typedQuery.setMaxResults(Math.toIntExact(pageSize));
            List<Customer> customers = typedQuery.getResultList();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(Customer.class)));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
            return PageUtil.createPage(pageNumber, pageSize, customers, totalElements);

        }catch (Exception exception){
            throw new GeneralFailureException(GeneralFailureException.ERROR_FETCH,
                    Map.of("original message", exception.getMessage().substring(0, 15),
                            "error", "there was an error with your request"));
        }

    }

    @Override
    public List<Customer> get() {
        return entityManager.createQuery("SELECT e FROM Customer e", Customer.class).getResultList();
    }

    @Override
    public Customer update(Customer customer) {
        try {
            entityManager.getTransaction().begin();
            Customer updatedCustomer = entityManager.merge(customer);
            entityManager.getTransaction().commit();
            return updatedCustomer;

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
            TypedQuery<Customer> customerTypedQuery = entityManager.createQuery("SELECT e from Customer e WHERE e.user.refNo = :refNo", Customer.class);
            customerTypedQuery.setParameter("refNo", refNo);
            Customer customer = customerTypedQuery.getSingleResult();
            if (customer != null) {
                entityManager.remove(customer);
            }
            return refNo;
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_DELETE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't delete entity"));
        }
    }

    @Override
    public Long checkCustomerAssociation(Customer customer) {
        return null;
    }

    @Override
    public Set<Customer> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }

        TypedQuery<Customer> query = entityManager.createQuery(
                "SELECT a FROM Customer a WHERE a.user.refNo IN :refNos", Customer.class);
        query.setParameter("refNos", refNos);
        List<Customer> accounts = query.getResultList();

        return new HashSet<>(accounts);
    }

    @Override
    public boolean existByPocket(Pocket pocket) {
        if (pocket == null || pocket.getId() == null) {
            return false;
        }
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT count(a) FROM Customer a WHERE :pocket in  a.pockets ", Long.class);
        query.setParameter("pocket", pocket);
        try {
            return query.getSingleResult() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public boolean existByExpense(Expense expense) {
        if (expense == null || expense.getId() == null) {
            return false;
        }
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT count(a) FROM Customer a WHERE :expense in  a.expenses ", Long.class);
        query.setParameter("expense", expense);
        try {
            return query.getSingleResult() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }
}