package name.expenses.utils;

public class ValidateInputUtils {
    public static boolean isValidInput(Object category, Object association){
        return category != null && association != null;
    }
}