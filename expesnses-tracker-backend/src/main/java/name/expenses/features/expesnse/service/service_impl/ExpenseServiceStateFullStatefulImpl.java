package name.expenses.features.expesnse.service.service_impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Stateful;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.ObjectMapperConfig;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseServiceStateFull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Stateful
@Transactional
public class ExpenseServiceStateFullStatefulImpl implements ExpenseServiceStateFull {
    private final ObjectMapper objectMapper = ObjectMapperConfig.getObjectMapper();
    private final Map<Long, Expense> expenseDB =
            new ConcurrentHashMap<Long, Expense>();
    private final AtomicInteger idCounter = new AtomicInteger();


    @Override
    public Response createExpenseFromJSON(InputStream is) throws IOException {
        Expense expense = objectMapper.readValue(is, Expense.class);
        expense.setId((long) idCounter.incrementAndGet());
        expenseDB.put(expense.getId(), expense);
        System.out.println("Created expense " + expense.getId());
        System.out.println(expense);
        return Response
                .ok(
                        objectMapper.writeValueAsString(expense),
                        MediaType.APPLICATION_JSON_TYPE
                        )
                .build();
    }
    @Override
    public Response createExpenseFromXML(InputStream is) {
        Expense expense = getExpense(is);
        System.out.println(expense);
        expense.setId((long) idCounter.incrementAndGet());
        expenseDB.put(expense.getId(), expense);
        return Response.ok(expense, MediaType.APPLICATION_JSON_TYPE).build();
    }

    private static Expense getExpense(InputStream is) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();

            Expense expense = new Expense();
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element) {
                    Element element = (Element) nodes.item(i);
                    String tagName = element.getTagName();
                    switch (tagName) {
                        case "name":
                            expense.setName(element.getTextContent());
                            break;
                        case "amount":
                            String amountValue = element.getTextContent();
                            if (!amountValue.isEmpty()) {
                                expense.setAmount(Double.parseDouble(amountValue));
                            }
                            break;
                    }
                }
            }

            return expense;
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }
}
