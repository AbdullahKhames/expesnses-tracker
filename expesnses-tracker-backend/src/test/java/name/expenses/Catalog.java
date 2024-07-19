package name.expenses;

import name.expenses.features.account.models.Account;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.models.BudgetType;
import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.features.budget_transfer.models.BudgetTransfer;
import name.expenses.features.category.models.Category;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.budget_transfer.models.AmountType;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.features.user.models.User;

import java.util.*;

public class Catalog {
    private boolean init = false;

    private static final BudgetAmount budgetAmount1 = new BudgetAmount();
    private static final BudgetAmount budgetAmount2 = new BudgetAmount();
    private static final BudgetAmount budgetAmount3 = new BudgetAmount();
    private static final BudgetAmount budgetAmount4 = new BudgetAmount();
    private static final BudgetAmount budgetAmount5 = new BudgetAmount();
    private static final BudgetAmount budgetAmount6 = new BudgetAmount();
    private static final BudgetAmount budgetAmount7 = new BudgetAmount();
    private static final BudgetAmount budgetAmount8 = new BudgetAmount();

    private static final Map<Integer, BudgetAmount> budgetAmounts = new HashMap<>(Map.of(
            1, budgetAmount1,
            2, budgetAmount2,
            3, budgetAmount3,
            4, budgetAmount4,
            5, budgetAmount5,
            6, budgetAmount6,
            7, budgetAmount7,
            8, budgetAmount8
));

    private static final Budget budget1 = new Budget();
    private static final Budget budget2 = new Budget();
    private static final Budget budget3 = new Budget();
    private static final Budget budget4 = new Budget();
    private static final Budget budget5 = new Budget();
    private static final Budget budget6 = new Budget();
    private static final Budget budget7 = new Budget();
    private static final Budget budget8 = new Budget();


    private static final Map<Integer, Budget> budgets = new HashMap<>(Map.of(
            1, budget1,
            2, budget2,
            3, budget3,
            4, budget4,
            5, budget5,
            6, budget6,
            7, budget7,
            8, budget8));

    private static final Expense expense1 = new Expense();
    private static final Expense expense2 = new Expense();
    private static final Expense expense3 = new Expense();
    private static final Expense expense4 = new Expense();
    private static final Expense expense5 = new Expense();
    private static final Expense expense6 = new Expense();

    private static final Map<Integer, Expense> expenses = new HashMap<>(Map.of(
            1, expense1,
            2, expense2,
            3, expense3,
            4, expense4,
            5, expense5,
            6, expense6
));

    private static final Account account1 = new Account();
    private static final Account account2 = new Account();
    private static final Account account3 = new Account();
    private static final Map<Integer, Account> accounts = new HashMap<>(Map.of(
            1, account1,
            2, account2,
            3, account3));


    private static final Category category1 = new Category();
    private static final Category category2 = new Category();
    private static final Category category3 = new Category();
    private static final Map<Integer, Category> categories = new HashMap<>(Map.of(
            1, category1,
            2, category2,
            3, category3));


    private static final SubCategory subCategory1 = new SubCategory();
    private static final SubCategory subCategory2 = new SubCategory();
    private static final SubCategory subCategory3 = new SubCategory();
    private static final Map<Integer, SubCategory> subCategories = new HashMap<>(Map.of(
            1, subCategory1,
            2, subCategory2,
            3, subCategory3));


    private static final Transaction transaction1 = new Transaction();
    private static final Transaction transaction2 = new Transaction();
    private static final Transaction transaction3 = new Transaction();
    private static final Map<Integer, Transaction> transactions = new HashMap<>(Map.of(
            1, transaction1,
            2, transaction2,
            3, transaction3));


    private static final BudgetTransfer budgetTransfer1 = new BudgetTransfer();
    private static final BudgetTransfer budgetTransfer2 = new BudgetTransfer();
    private static final BudgetTransfer budgetTransfer3 = new BudgetTransfer();
    private static final Map<Integer, BudgetTransfer> budgetTransfers = new HashMap<>(Map.of(
            1, budgetTransfer1,
            2, budgetTransfer2,
            3, budgetTransfer3));


    private static final User user1 = new User();
    private static final Map<Integer, User> users = new HashMap<>(Map.of(
            1, user1));


    private static final Customer customer1 = new Customer();
    private static final Map<Integer, Customer> customers = new HashMap<>(Map.of(
            1, customer1));




    private void init(){
        if (!init){
            user1.setCustomer(customer1);

            expense1.setName("expense1");
            expense1.setDetails("expense1 details");
            expense1.setAmount(50);
            expense1.setCustomer(customer1);
            expense1.setSubCategory(subCategory1);

            expense2.setName("expense2");
            expense2.setDetails("expense2 details");
            expense2.setAmount(100);
            expense2.setCustomer(customer1);
            expense2.setSubCategory(subCategory1);

            expense3.setName("expense3");
            expense3.setDetails("expense3 details");
            expense3.setAmount(150);
            expense3.setCustomer(customer1);
            expense3.setSubCategory(subCategory2);

            expense4.setName("expense4");
            expense4.setDetails("expense4 details");
            expense4.setAmount(200);
            expense4.setCustomer(customer1);
            expense4.setSubCategory(subCategory2);

            expense5.setName("expense5");
            expense5.setDetails("expense5 details");
            expense5.setAmount(250);
            expense5.setCustomer(customer1);
            expense5.setSubCategory(subCategory3);

            expense6.setName("expense6");
            expense6.setDetails("expense6 details");
            expense6.setAmount(300);
            expense6.setCustomer(customer1);
            expense6.setSubCategory(subCategory3);

            subCategory1.setName("subcategory 1");
            subCategory1.setDetails("subcategory 1");
            subCategory1.setCategory(category1);
            subCategory1.getExpenses().addAll(new HashSet<>(Arrays.asList(expense1, expense2)));
            subCategory1.getCustomers().add(customer1);

            subCategory2.setName("subcategory 2");
            subCategory2.setDetails("subcategory 2");
            subCategory2.setCategory(category2);
            subCategory2.getExpenses().addAll(new HashSet<>(Arrays.asList(expense3, expense4)));
            subCategory2.getCustomers().add(customer1);

            subCategory3.setName("subcategory 3");
            subCategory3.setDetails("subcategory 3");
            subCategory3.setCategory(category3);
            subCategory3.getExpenses().addAll(new HashSet<>(Arrays.asList(expense5, expense6)));
            subCategory3.getCustomers().add(customer1);

            category1.setName("category 1");
            category1.setDetails("category1");
            category1.getSubCategories().addAll(new HashSet<>(List.of(subCategory1)));
            category1.getCustomers().add(customer1);

            category2.setName("category 2");
            category2.setDetails("category2");
            category2.getSubCategories().addAll(new HashSet<>(List.of(subCategory2)));
            category2.getCustomers().add(customer1);

            category3.setName("category 3");
            category3.setDetails("category3");
            category3.getSubCategories().addAll(new HashSet<>(List.of(subCategory3)));
            category3.getCustomers().add(customer1);

            budget1.setName("budget1");
            budget1.setDetails("EXTERNAL budget1");
            budget1.setBudgetType(BudgetType.EXTERNAL);
            budget1.setCustomer(customer1);
            budget1.setAmount(500.0);

            budget2.setName("budget2");
            budget2.setDetails("DEFAULT budget2");
            budget2.setBudgetType(BudgetType.DEFAULT);
            budget2.setAmount(150.0);
            budget2.setCustomer(customer1);

            budget3.setName("budget3");
            budget3.setDetails("BILLS budget3");
            budget3.setBudgetType(BudgetType.BILLS);
            budget3.setAmount(250.0);
            budget3.setCustomer(customer1);

            budget4.setName("budget4");
            budget4.setDetails("MOM budget4");
            budget4.setBudgetType(BudgetType.MOM);
            budget4.setAmount(350.0);
            budget4.setCustomer(customer1);

            budget5.setName("budget5");
            budget5.setDetails("ALLOWANCE budget5");
            budget5.setBudgetType(BudgetType.ALLOWANCE);
            budget5.setAmount(450.0);
            budget5.setCustomer(customer1);

            budget6.setName("budget6");
            budget6.setDetails("DONATION budget6");
            budget6.setBudgetType(BudgetType.DONATION);
            budget6.setAmount(550.0);
            budget6.setCustomer(customer1);

            budget7.setName("budget7");
            budget7.setDetails("ENTERTAINMENT budget7");
            budget7.setBudgetType(BudgetType.ENTERTAINMENT);
            budget7.setAmount(650.0);
            budget7.setCustomer(customer1);

            budget8.setName("budget8");
            budget8.setDetails("SAVINGS budget8");
            budget8.setBudgetType(BudgetType.SAVINGS);
            budget8.setAmount(750.0);
            budget8.setCustomer(customer1);



            account1.setName("account1");
            account1.setDetails("account1");
            account1.getCustomers().add(customer1);
            account1.getBudgets().addAll(new HashSet<>(Arrays.asList(budget1, budget2, budget3)));

            account2.setName("account2");
            account2.setDetails("account2");
            account2.getCustomers().add(customer1);
            account2.getBudgets().addAll(new HashSet<>(Arrays.asList(budget4, budget5, budget6)));

            account3.setName("account3");
            account3.setDetails("account3");
            account3.getCustomers().add(customer1);
            account3.getBudgets().addAll(new HashSet<>(Arrays.asList(budget7, budget8)));

            budgetAmount1.setAmountType(AmountType.DEBIT);
            budgetAmount1.setBudget(budget1);
            budgetAmount1.setAmount(50.0);
            budgetAmount1.setTrans(true);

            budgetAmount2.setAmountType(AmountType.CREDIT);
            budgetAmount2.setBudget(budget2);
            budgetAmount2.setAmount(25.0);
            budgetAmount2.setTrans(true);

            budgetAmount3.setAmountType(AmountType.CREDIT);
            budgetAmount3.setBudget(budget3);
            budgetAmount3.setAmount(20.0);
            budgetAmount3.setTrans(true);

            budgetAmount4.setAmountType(AmountType.CREDIT);
            budgetAmount4.setBudget(budget4);
            budgetAmount4.setAmount(5.0);

            budgetAmount5.setAmountType(AmountType.DEBIT);
            budgetAmount5.setBudget(budget5);
            budgetAmount5.setAmount(50.0);

            budgetAmount6.setAmountType(AmountType.DEBIT);
            budgetAmount6.setBudget(budget6);
            budgetAmount6.setAmount(50.0);

            budgetAmount7.setAmountType(AmountType.DEBIT);
            budgetAmount7.setBudget(budget7);
            budgetAmount7.setAmount(50.0);

            budgetAmount8.setAmountType(AmountType.DEBIT);
            budgetAmount8.setBudget(budget8);
            budgetAmount8.setAmount(50.0);


            budgetTransfer1.setName("budgetTransfer1");
            budgetTransfer1.setDetails("budgetTransfer1");
            budgetTransfer1.setLending(true);
            budgetTransfer1.setSenderBudgetAmount(budgetAmount1);
            budgetTransfer1.getReceiverBudgetAmounts().addAll(new HashSet<>(Arrays.asList(budgetAmount2, budgetAmount3, budgetAmount4)));
            budgetTransfer1.setAmount(50.0);
            budgetTransfer1.setCustomer(customer1);

            budgetTransfer2.setName("budgetTransfer2");
            budgetTransfer2.setDetails("budgetTransfer2");
            budgetTransfer2.setCustomer(customer1);

            budgetTransfer3.setName("budgetTransfer3");
            budgetTransfer3.setDetails("budgetTransfer3");
            budgetTransfer3.setCustomer(customer1);

            transaction1.setName("transaction1");
            transaction1.setDetails("transaction1");
            transaction1.setExpense(expense1);
            transaction1.setAmount(50.0);
            transaction1.getBudgetAmounts().addAll(new HashSet<>(List.of(budgetAmount5, budgetAmount6, budgetAmount4)));
            transaction1.setCustomer(customer1);

            transaction2.setName("transaction2");
            transaction2.setDetails("transaction2");
            transaction2.setExpense(expense2);
            transaction2.setAmount(50.0);
            transaction2.getBudgetAmounts().addAll(new HashSet<>(List.of(budgetAmount7)));
            transaction2.setCustomer(customer1);

            transaction3.setName("transaction3");
            transaction3.setDetails("transaction3");
            transaction3.setExpense(expense3);
            transaction3.setAmount(50.0);
            transaction3.getBudgetAmounts().addAll(new HashSet<>(List.of(budgetAmount8)));
            transaction3.setCustomer(customer1);



            customer1.setUser(user1);
            customer1.getBudgets().addAll(new HashSet<>(Arrays.asList(
                    budget1,
                    budget2,
                    budget3,
                    budget4,
                    budget5,
                    budget6,
                    budget7,
                    budget8
            )));
            customer1.getBudgetTransfers().addAll(new HashSet<>(Arrays.asList(
                    budgetTransfer1,
                    budgetTransfer2,
                    budgetTransfer3
            )));

            customer1.getTransactions().addAll(new HashSet<>(Arrays.asList(
                    transaction1,
                    transaction2,
                    transaction3
            )));

            customer1.getAccounts().addAll(new HashSet<>(Arrays.asList(account1, account2, account3)));
            customer1.getCategories().addAll(new HashSet<>(Arrays.asList(category1, category2, category3)));
            customer1.getSubCategories().addAll(new HashSet<>(Arrays.asList(subCategory1, subCategory2, subCategory3)));
            customer1.getExpenses().addAll(new HashSet<>(Arrays.asList(
                    expense1,
                    expense2,
                    expense3,
                    expense4,
                    expense5,
                    expense6
            )));
        }
    }

    public  <T> T getObject(Class<T> entityClass, int index){
        init();
        return switch (entityClass.getSimpleName()){
            case "Transaction" -> (T) transactions.get(index);
            case "budgetTransfer" -> (T) budgetTransfers.get(index);
            case "Expense" -> (T) expenses.get(index);
            case "Account" -> (T) accounts.get(index);
            case "Budget" -> (T) budgets.get(index);
            case "Category" -> (T) categories.get(index);
            case "SubCategory" -> (T) subCategories.get(index);
            case "budgetAmount" -> (T) budgetAmounts.get(index);
            case "Customer" -> (T) customers.get(index);
            case "User" -> (T) users.get(index);
            case null -> throw new IllegalStateException("Unexpected value: cannot be null");
            default -> throw new IllegalStateException("Unexpected value: " + entityClass.getSimpleName());
        };
    }
}
