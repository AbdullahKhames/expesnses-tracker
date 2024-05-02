package name.expenses;

import name.expenses.features.account.models.Account;
import name.expenses.features.category.models.Category;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.pocket.models.PocketType;
import name.expenses.features.pocket_transfer.models.AmountType;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.features.user.models.User;

import java.util.*;

public class Catalog {
    private boolean init = false;

    private static final PocketAmount pocketAmount1 = new PocketAmount();
    private static final PocketAmount pocketAmount2 = new PocketAmount();
    private static final PocketAmount pocketAmount3 = new PocketAmount();
    private static final PocketAmount pocketAmount4 = new PocketAmount();
    private static final PocketAmount pocketAmount5 = new PocketAmount();
    private static final PocketAmount pocketAmount6 = new PocketAmount();
    private static final PocketAmount pocketAmount7 = new PocketAmount();
    private static final PocketAmount pocketAmount8 = new PocketAmount();

    private static final Map<Integer, PocketAmount> pocketAmounts = new HashMap<>(Map.of(
            1, pocketAmount1,
            2, pocketAmount2,
            3, pocketAmount3,
            4, pocketAmount4,
            5, pocketAmount5,
            6, pocketAmount6,
            7, pocketAmount7,
            8, pocketAmount8
));

    private static final Pocket pocket1 = new Pocket();
    private static final Pocket pocket2 = new Pocket();
    private static final Pocket pocket3 = new Pocket();
    private static final Pocket pocket4 = new Pocket();
    private static final Pocket pocket5 = new Pocket();
    private static final Pocket pocket6 = new Pocket();
    private static final Pocket pocket7 = new Pocket();
    private static final Pocket pocket8 = new Pocket();


    private static final Map<Integer, Pocket> pockets = new HashMap<>(Map.of(
            1, pocket1,
            2, pocket2,
            3, pocket3,
            4, pocket4,
            5, pocket5,
            6, pocket6,
            7, pocket7,
            8, pocket8));

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


    private static final PocketTransfer pocketTransfer1 = new PocketTransfer();
    private static final PocketTransfer pocketTransfer2 = new PocketTransfer();
    private static final PocketTransfer pocketTransfer3 = new PocketTransfer();
    private static final Map<Integer, PocketTransfer> pocketTransfers = new HashMap<>(Map.of(
            1, pocketTransfer1,
            2, pocketTransfer2,
            3, pocketTransfer3));


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

            pocket1.setName("pocket1");
            pocket1.setDetails("EXTERNAL pocket1");
            pocket1.setPocketType(PocketType.EXTERNAL);
            pocket1.setCustomer(customer1);
            pocket1.setAmount(50.0);

            pocket2.setName("pocket2");
            pocket2.setDetails("DEFAULT pocket2");
            pocket2.setPocketType(PocketType.DEFAULT);
            pocket2.setAmount(150.0);
            pocket2.setCustomer(customer1);

            pocket3.setName("pocket3");
            pocket3.setDetails("BILLS pocket3");
            pocket3.setPocketType(PocketType.BILLS);
            pocket3.setAmount(250.0);
            pocket3.setCustomer(customer1);

            pocket4.setName("pocket4");
            pocket4.setDetails("MOM pocket4");
            pocket4.setPocketType(PocketType.MOM);
            pocket4.setAmount(350.0);
            pocket4.setCustomer(customer1);

            pocket5.setName("pocket5");
            pocket5.setDetails("ALLOWANCE pocket5");
            pocket5.setPocketType(PocketType.ALLOWANCE);
            pocket5.setAmount(450.0);
            pocket5.setCustomer(customer1);

            pocket6.setName("pocket6");
            pocket6.setDetails("DONATION pocket6");
            pocket6.setPocketType(PocketType.DONATION);
            pocket6.setAmount(550.0);
            pocket6.setCustomer(customer1);

            pocket7.setName("pocket7");
            pocket7.setDetails("ENTERTAINMENT pocket7");
            pocket7.setPocketType(PocketType.ENTERTAINMENT);
            pocket7.setAmount(650.0);
            pocket7.setCustomer(customer1);

            pocket8.setName("pocket8");
            pocket8.setDetails("SAVINGS pocket8");
            pocket8.setPocketType(PocketType.SAVINGS);
            pocket8.setAmount(750.0);
            pocket8.setCustomer(customer1);



            account1.setName("account1");
            account1.setDetails("account1");
            account1.getCustomers().add(customer1);
            account1.getPockets().addAll(new HashSet<>(Arrays.asList(pocket1, pocket2, pocket3)));

            account2.setName("account2");
            account2.setDetails("account2");
            account2.getCustomers().add(customer1);
            account2.getPockets().addAll(new HashSet<>(Arrays.asList(pocket4, pocket5, pocket6)));

            account3.setName("account3");
            account3.setDetails("account3");
            account3.getCustomers().add(customer1);
            account3.getPockets().addAll(new HashSet<>(Arrays.asList(pocket7, pocket8)));

            pocketAmount1.setAmountType(AmountType.DEBIT);
            pocketAmount1.setPocket(pocket1);
            pocketAmount1.setAmount(50.0);
            pocketAmount1.setTrans(true);

            pocketAmount2.setAmountType(AmountType.CREDIT);
            pocketAmount2.setPocket(pocket2);
            pocketAmount2.setAmount(25.0);
            pocketAmount2.setTrans(true);

            pocketAmount3.setAmountType(AmountType.CREDIT);
            pocketAmount3.setPocket(pocket3);
            pocketAmount3.setAmount(20.0);
            pocketAmount3.setTrans(true);

            pocketAmount4.setAmountType(AmountType.CREDIT);
            pocketAmount4.setPocket(pocket4);
            pocketAmount4.setAmount(5.0);

            pocketAmount5.setAmountType(AmountType.DEBIT);
            pocketAmount5.setPocket(pocket5);
            pocketAmount5.setAmount(50.0);

            pocketAmount6.setAmountType(AmountType.DEBIT);
            pocketAmount6.setPocket(pocket6);
            pocketAmount6.setAmount(50.0);

            pocketAmount7.setAmountType(AmountType.DEBIT);
            pocketAmount7.setPocket(pocket7);
            pocketAmount7.setAmount(50.0);

            pocketAmount8.setAmountType(AmountType.DEBIT);
            pocketAmount8.setPocket(pocket8);
            pocketAmount8.setAmount(50.0);


            pocketTransfer1.setName("pocketTransfer1");
            pocketTransfer1.setDetails("pocketTransfer1");
            pocketTransfer1.setLending(true);
            pocketTransfer1.setSenderPocketAmount(pocketAmount1);
            pocketTransfer1.getReceiverPocketAmounts().addAll(new HashSet<>(Arrays.asList(pocketAmount2, pocketAmount3, pocketAmount4)));
            pocketTransfer1.setAmount(50.0);
            pocketTransfer1.setCustomer(customer1);

            pocketTransfer2.setName("pocketTransfer2");
            pocketTransfer2.setDetails("pocketTransfer2");
            pocketTransfer2.setCustomer(customer1);

            pocketTransfer3.setName("pocketTransfer3");
            pocketTransfer3.setDetails("pocketTransfer3");
            pocketTransfer3.setCustomer(customer1);

            transaction1.setName("transaction1");
            transaction1.setDetails("transaction1");
            transaction1.setExpense(expense1);
            transaction1.setAmount(50.0);
            transaction1.setPocketAmounts(new HashSet<>(List.of(pocketAmount5, pocketAmount6)));
            transaction1.setCustomer(customer1);

            transaction2.setName("transaction2");
            transaction2.setDetails("transaction2");
            transaction2.setExpense(expense2);
            transaction2.setAmount(50.0);
            transaction2.setPocketAmounts(new HashSet<>(List.of(pocketAmount7)));
            transaction2.setCustomer(customer1);

            transaction3.setName("transaction3");
            transaction3.setDetails("transaction3");
            transaction3.setExpense(expense3);
            transaction3.setAmount(50.0);
            transaction3.setPocketAmounts(new HashSet<>(List.of(pocketAmount8)));
            transaction3.setCustomer(customer1);



            customer1.setUser(user1);
            customer1.getPockets().addAll(new HashSet<>(Arrays.asList(
                    pocket1,
                    pocket2,
                    pocket3,
                    pocket4,
                    pocket5,
                    pocket6,
                    pocket7,
                    pocket8
            )));
            customer1.getPocketTransfers().addAll(new HashSet<>(Arrays.asList(
                    pocketTransfer1,
                    pocketTransfer2,
                    pocketTransfer3
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
            case "PocketTransfer" -> (T) pocketTransfers.get(index);
            case "Expense" -> (T) expenses.get(index);
            case "Account" -> (T) accounts.get(index);
            case "Pocket" -> (T) pockets.get(index);
            case "Category" -> (T) categories.get(index);
            case "SubCategory" -> (T) subCategories.get(index);
            case "PocketAmount" -> (T) pocketAmounts.get(index);
            case "Customer" -> (T) customers.get(index);
            case "User" -> (T) users.get(index);
            case null -> throw new IllegalStateException("Unexpected value: cannot be null");
            default -> throw new IllegalStateException("Unexpected value: " + entityClass.getSimpleName());
        };
    }
}
