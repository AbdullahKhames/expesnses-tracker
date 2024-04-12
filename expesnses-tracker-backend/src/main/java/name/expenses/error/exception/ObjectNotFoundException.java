package name.expenses.error.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}