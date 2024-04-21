package name.expenses.services.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import name.expenses.models.*;
import org.hibernate.Session;

@Transactional
@Slf4j
@Setter
public class CurrentCustomerCollections {
    @PersistenceContext(name = "expenses-unit")
    private EntityManager entityManager;

    public boolean isPresentCollection(User user, Object currentUserReg,
                                       Models models){
        try {
            Customer customer = user.getCustomer();
            Session session = (Session) entityManager.getDelegate();
            return switch (models) {
                case POCKET ->
                        !session.createQuery("select p from Pocket p join fetch p.customer c where c = :customer and p = :pocket", Pocket.class)
                                .setParameter("customer", customer)
                                .setParameter("pocket", (Pocket) currentUserReg)
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
