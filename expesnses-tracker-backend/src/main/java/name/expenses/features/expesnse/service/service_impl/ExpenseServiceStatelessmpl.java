package name.expenses.features.expesnse.service.service_impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.ObjectMapperConfig;
import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ExpenseServiceStatelessmpl implements ExpenseService {
    public static final String EXPENSE = "Expense";
    private final ExpenseDAO expenseDAO;
    private final ExpenseMapper expenseMapper;
    @Override
    public ResponseDto createExpense(ExpenseReqDto expense) {
        Expense sentExpense = expenseMapper.reqDtoToEntity(expense);
        Expense savedExpense = expenseDAO.createExpense(sentExpense);
        log.info("created expense {}", savedExpense);
        return ResponseDtoBuilder.getCreateResponse(EXPENSE, savedExpense.getRefNo(), expenseMapper.entityToRespDto(savedExpense));
    }


    @Override
    public ResponseDto getExpense(String refNo) {
        Expense expense = expenseDAO.getExpense(refNo);
        log.info("fetched expense {}", expense);
        return ResponseDtoBuilder.getFetchResponse(EXPENSE, expense.getRefNo(), expenseMapper.entityToRespDto(expense));
    }

    @Override
    public ResponseDto updateExpense(String refNo, ExpenseUpdateDto expenseUpdateDto) {
        Expense expense = expenseDAO.getExpense(refNo);
        log.info("fetched expense {}", expense);
        expenseMapper.update(expense, expenseUpdateDto);
        log.info("updated expense {}", expense);
        return ResponseDtoBuilder.getUpdateResponse(EXPENSE, expense.getRefNo(), expenseMapper.entityToRespDto(expenseDAO.updateExpense(expense)));
    }

    @Override
    public ResponseDto deleteExpense(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(EXPENSE,expenseDAO.deleteExpense(refNo));
    }

    @Override
    public ResponseDto getAllEntities(int pageNumber, int pageSize, String sortBy, SortDirection sortDirection) {
        Page<Expense> expensePage = expenseDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);

//        List<ExpenseRespDto> expenseDtos = expensePage.getContent().stream()
//                .map(expenseMapper::entityToRespDto)
//                .collect(Collectors.toList());
        Page<ExpenseRespDto> expenseDtos = expenseMapper.entityToRespDto(expensePage);
        return ResponseDtoBuilder.getFetchAllResponse(EXPENSE, expenseDtos);
    }
}
