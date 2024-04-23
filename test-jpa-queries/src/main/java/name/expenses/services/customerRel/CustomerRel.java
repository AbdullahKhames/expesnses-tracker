package name.expenses.services.customerRel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import name.expenses.models.*;
import name.expenses.services.utils.FieldValidator;
import name.expenses.services.utils.PageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Transactional
@Slf4j
@Setter
public class CustomerRel {
    @PersistenceContext(name = "expenses-unit")
    private EntityManager entityManager;
    public <T> Page<T> getAllEntitiesForCustomer(Class<T> entityClass, Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {

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

        // Handle different types of relationships with customers
        Predicate predicate = null;

        if (hasManyToOneRelationship(entityClass)) {
            predicate = cb.equal(root.get("customer").get("id"), customerId);
        } else if (hasManyToManyRelationship(entityClass)) {
            Join<T, Customer> customerJoin = root.join("customers", JoinType.LEFT); // Use appropriate JoinType
            predicate = cb.equal(customerJoin.get("id"), customerId);
        }

        if (predicate != null) {
            cq.where(predicate);
        }

        List<T> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) ((pageNumber - 1) * pageSize))
                .setMaxResults(pageSize.intValue())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));
        Predicate countPredicate = null;

        // Handle different types of relationships with customers
        if (hasManyToOneRelationship(entityClass)) {
            countPredicate = cb.equal(countRoot.get("customer").get("id"), customerId);
        } else if (hasManyToManyRelationship(entityClass)) {
            Join<T, Customer> customerJoin = countRoot.join("customers", JoinType.LEFT); // Use appropriate JoinType
            countPredicate = cb.equal(customerJoin.get("id"), customerId);
        }

        if (countPredicate != null) {
            countQuery.where(countPredicate);
        }

        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return PageUtil.createPage(pageNumber, pageSize, resultList, count);
    }
    private <T> boolean hasManyToOneRelationship(Class<T> entityClass) {
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(Expense.class, Pocket.class, PocketTransfer.class, Transaction.class));
        return classes.contains(entityClass);
    }

    private <T> boolean hasManyToManyRelationship(Class<T> entityClass) {
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(Account.class, Category.class, SubCategory.class));
        return classes.contains(entityClass);
    }
    public Page<Account> getAllCustomerAccounts(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Account.class, customerId, pageNumber, pageSize, sortBy, sortDirection);
    }

    public Page<Category> getAllCustomerCategories(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Category.class, customerId, pageNumber, pageSize, sortBy, sortDirection);
    }


    public Page<Expense> getAllCustomerExpenses(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Expense.class, customerId, pageNumber, pageSize, sortBy, sortDirection);    }


    public Page<SubCategory> getAllCustomerSubCategories(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(SubCategory.class, customerId, pageNumber, pageSize, sortBy, sortDirection);    }


    public Page<Pocket> getAllCustomerPockets(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Pocket.class, customerId, pageNumber, pageSize, sortBy, sortDirection);    }


    public Page<PocketTransfer> getAllCustomerPocketTransfers(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(PocketTransfer.class, customerId, pageNumber, pageSize, sortBy, sortDirection);    }


    public Page<Transaction> getAllCustomerTransactions(Long customerId, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        return getAllEntitiesForCustomer(Transaction.class, customerId, pageNumber, pageSize, sortBy, sortDirection);    }

}
