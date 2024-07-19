package name.expenses.utils;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.account.models.Account;
import name.expenses.features.association.Models;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.category.models.Category;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.user.models.User;
import org.hibernate.Session;

@Singleton
@Transactional
@Slf4j
public class CurrentCustomerCollections {
    @PersistenceContext(name = "expenses-unit")
    private EntityManager entityManager;
    @Context
    private SecurityContext securityContext;
    public boolean isPresentCollection(Object currentUserReg,
                                       Models models){
        User user;
        try {
            user = (User)securityContext.getUserPrincipal();
            Customer customer = user.getCustomer();
            Session session = (Session) entityManager.getDelegate();
            return switch (models) {
                case Budget ->
                        !session.createQuery("select p from Budget p join fetch p.customer c where c = :customer and p = :budget", Budget.class)
                                .setParameter("customer", customer)
                                .setParameter("Budget", (Budget) currentUserReg)
                                .getResultList().isEmpty();
                case EXPENSE ->
                        !session.createQuery("select e from Expense e join fetch e.customer c where c = :customer and e = :expense", Expense.class)
                                .setParameter("customer", customer)
                                .setParameter("expense", (Expense) currentUserReg)
                                .getResultList().isEmpty();
                case CATEGORY ->
                        !session.createQuery("select c from Category c join fetch c.customers cu where cu = :customer and c = :category", Category.class)
                                .setParameter("customer", customer)
                                .setParameter("category", (Category) currentUserReg)
                                .getResultList().isEmpty();
                case SUB_CATEGORY ->
                        !session.createQuery("select s from SubCategory s join fetch s.customers c where c = :customer and s = :subCategory", SubCategory.class)
                                .setParameter("customer", customer)
                                .setParameter("subCategory", (SubCategory) currentUserReg)
                                .getResultList().isEmpty();
                case ACCOUNT ->
                        !session.createQuery("select a from Account a join fetch a.customers c where c = :customer and a = :account", Account.class)
                                .setParameter("customer", customer)
                                .setParameter("account", (Account) currentUserReg)
                                .getResultList().isEmpty();
                default -> false;
            };
        } catch (Exception exception) {
            return false;
        }
    }
}
