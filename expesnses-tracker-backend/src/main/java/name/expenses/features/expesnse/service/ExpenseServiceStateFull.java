package name.expenses.features.expesnse.service;

import jakarta.ejb.Local;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;

@Local
public interface ExpenseServiceStateFull {
    public Response createExpenseFromXML(InputStream is);

    Response createExpenseFromJSON(InputStream is) throws IOException;
}
