package name.expenses.globals;

import name.expenses.globals.responses.ResponseDto;

import java.util.Optional;

public interface ObjectGetter<T> {
    Optional<T> get(String refNo);
}
