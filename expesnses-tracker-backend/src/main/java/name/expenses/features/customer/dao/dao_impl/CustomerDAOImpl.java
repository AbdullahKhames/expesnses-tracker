package name.expenses.features.customer.dao.dao_impl;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import name.expenses.config.advice.RepoAdvice;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.features.account.models.Account;
import name.expenses.features.category.models.Category;
import name.expenses.features.customer.dao.CustomerDAO;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.transaction.models.Transaction;
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
    public Customer getCustomerWithSubCategories(Long customerId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);

        Root<Customer> customer = cq.from(Customer.class);
        customer.fetch("subCategories", JoinType.LEFT);

        cq.select(customer).where(cb.equal(customer.get("id"), customerId));

        return entityManager.createQuery(cq).getSingleResult();
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
    public <T, J> Page<T> getAllEntitiesForCustomer(Class<T> entityClass, Class<J> otherEntityClass, Optional<String> optionalRef, Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        cq.select(root);
        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, entityClass)) {
            sortByPath = root.get(sortBy);
        } else {
            sortByPath = root.get("id");
        }

        if (sortDirection == SortDirection.ASC) {
            cq.orderBy(cb.asc(sortByPath));
        } else {
            cq.orderBy(cb.desc(sortByPath));
        }

        Predicate[] predicate = new Predicate[2];

        if (hasManyToOneRelationship(entityClass)) {
            predicate[0] = cb.equal(root.get("customer").get("id"), customerId);
        } else if (hasManyToManyRelationship(entityClass)) {
            Join<T, Customer> customerJoin = root.join("customers", JoinType.LEFT);
            predicate[0] = cb.equal(customerJoin.get("id"), customerId);
        }
        optionalRef.ifPresent(ref -> {
            if (otherEntityClass != null){
                String className = getClassName(otherEntityClass);
                predicate[1] = cb.equal(root.get(className).get("refNo"), ref);
            }
        });

        if (predicate[0] != null && predicate[1] != null) {
            cq.where(predicate);
        } else if (predicate[0] != null) {
            cq.where(predicate[0]);
        } else if (predicate[1] != null) {
            cq.where(predicate[1]);
        }


        List<T> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) ((pageNumber - 1) * pageSize))
                .setMaxResults(pageSize.intValue())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));
        Predicate[] countPredicate = new Predicate[2];

        if (hasManyToOneRelationship(entityClass)) {
            countPredicate[0] = cb.equal(countRoot.get("customer").get("id"), customerId);
        } else if (hasManyToManyRelationship(entityClass)) {
            Join<T, Customer> customerJoin = countRoot.join("customers", JoinType.LEFT);
            countPredicate[0] = cb.equal(customerJoin.get("id"), customerId);
        }
        optionalRef.ifPresent(ref -> {
            if (otherEntityClass != null){
                String className = getClassName(otherEntityClass);
                countPredicate[1] = cb.equal(countRoot.get(className).get("refNo"), ref);
            }
        });
        if (countPredicate[0] != null && countPredicate[1] != null) {
            countQuery.where(countPredicate);
        } else if (countPredicate[0] != null) {
            countQuery.where(countPredicate[0]);
        } else if (countPredicate[1] != null) {
            countQuery.where(countPredicate[1]);
        }

        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return PageUtil.createPage(pageNumber, pageSize, resultList, count);
    }

    private static <J> String getClassName(Class<J> otherEntityClass) {
        String s = otherEntityClass.getSimpleName();
        return s.replace(s.charAt(0), Character.toLowerCase(s.charAt(0)));
    }
    private <T> boolean hasManyToOneRelationship(Class<T> entityClass) {
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(Expense.class, Pocket.class, PocketTransfer.class, Transaction.class));
        return classes.contains(entityClass);
    }

    private <T> boolean hasManyToManyRelationship(Class<T> entityClass) {
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(Account.class, Category.class, SubCategory.class));
        return classes.contains(entityClass);
    }
    @Override
    public Page<Account> getAllCustomerAccounts(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Account.class, null, Optional.empty(), customerId, pageNumber, pageSize, sortBy, sortDirection);
    }

    @Override
    public Page<Category> getAllCustomerCategories(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Category.class, null, Optional.empty(), customerId, pageNumber, pageSize, sortBy, sortDirection);
    }

    @Override
    public Page<Expense> getAllCustomerExpenses(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Expense.class, null, Optional.empty(), customerId, pageNumber, pageSize, sortBy, sortDirection);    }

    @Override
    public Page<SubCategory> getAllCustomerSubCategories(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(SubCategory.class, null, Optional.empty(), customerId, pageNumber, pageSize, sortBy, sortDirection);    }

    @Override
    public Page<Pocket> getAllCustomerPockets(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Pocket.class, null, Optional.empty(), customerId, pageNumber, pageSize, sortBy, sortDirection);    }

    @Override
    public Page<PocketTransfer> getAllCustomerPocketTransfers(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(PocketTransfer.class, null, Optional.empty(), customerId, pageNumber, pageSize, sortBy, sortDirection);    }

    @Override
    public Page<Transaction> getAllCustomerTransactions(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Transaction.class, null, Optional.empty(), customerId, pageNumber, pageSize, sortBy, sortDirection);    }

    @Override
    public Page<Expense> getAllCustomerSubCategoryExpenses(Long currentCustomerId, String subCategoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Expense.class, SubCategory.class, Optional.of(subCategoryRef), currentCustomerId, pageNumber, pageSize, sortBy, sortDirection);
    }

    @Override
    public Page<SubCategory> getAllCustomerCategorySubCategories(Long currentCustomerId, String categoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(SubCategory.class, Category.class, Optional.of(categoryRef), currentCustomerId, pageNumber, pageSize, sortBy, sortDirection);
    }

    @Override
    public Page<Pocket> getAllCustomerAccountPockets(Long currentCustomerId, String accountRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Pocket.class, Account.class, Optional.of(accountRef), currentCustomerId, pageNumber, pageSize, sortBy, sortDirection);
    }
}