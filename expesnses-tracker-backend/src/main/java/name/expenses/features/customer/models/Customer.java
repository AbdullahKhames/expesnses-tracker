package name.expenses.features.customer.models;

import jakarta.persistence.*;
import lombok.*;
import name.expenses.features.account.models.Account;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget_transfer.models.BudgetTransfer;
import name.expenses.features.category.models.Category;
import name.expenses.features.expesnse.models.Expense;

import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.features.user.models.User;
import name.expenses.utils.collection_getter.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements
        UserGetter,
        AccountGetter,
        BudgetGetter,
        CategoryGetter,
        SubCategoryGetter,
        ExpenseGetter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Customer_accounts",
                joinColumns = @JoinColumn(name = "customer_id", nullable = false),
                inverseJoinColumns = @JoinColumn(name = "account_id", nullable = false))
    @ToString.Exclude
    private Set<Account> accounts = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Set<Budget> budgets = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Customer_categories",
            joinColumns = @JoinColumn(name = "customer_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id", nullable = false))
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Customer_sub_categories",
            joinColumns = @JoinColumn(name = "customer_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "sub_category_id", nullable = false))
    @ToString.Exclude
    private Set<SubCategory> subCategories = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Set<Expense> expenses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Set<Transaction> transactions = new HashSet<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Set<BudgetTransfer> budgetTransfers = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Customer customer = (Customer) o;
        return getUser() != null && Objects.equals(getUser(), customer.getUser());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String getRefNo() {
        return user.getRefNo();
    }
}
