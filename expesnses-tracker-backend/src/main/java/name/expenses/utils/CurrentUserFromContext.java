package name.expenses.utils;

import jakarta.ws.rs.core.SecurityContext;
import name.expenses.features.account.models.Account;
import name.expenses.features.association.Models;
import name.expenses.features.category.models.Category;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.user.models.User;
import name.expenses.globals.CurrentUserReg;


public class CurrentUserFromContext {

    public static boolean getCurrentUserFromContext(SecurityContext securityContext,
                                                    Object currentUserReg,
                                                    Models models){
        User user;
        try {
            user = (User)securityContext.getUserPrincipal();
            return switch (models){
                case POCKET -> user.getCustomer().getPockets().contains((Pocket) currentUserReg);
                case EXPENSE ->user.getCustomer().getExpenses().contains((Expense) currentUserReg);
                case CATEGORY ->user.getCustomer().getCategories().contains((Category) currentUserReg);
                case SUB_CATEGORY ->user.getCustomer().getSubCategories().contains((SubCategory) currentUserReg);
                case ACCOUNT ->user.getCustomer().getAccounts().contains((Account) currentUserReg);
                default -> false;
            };
        }catch (Exception exception){
            return false;
        }
    }
}
