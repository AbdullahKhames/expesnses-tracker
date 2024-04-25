package name.expenses.exceptions;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}