package name.expenses.features.customer.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.account.dtos.response.AccountRespDto;
import name.expenses.features.account.mappers.AccountMapper;
import name.expenses.features.account.models.Account;
import name.expenses.features.account.service.AccountService;
import name.expenses.features.association.Models;
import name.expenses.features.budget.dtos.response.BudgetRespDto;
import name.expenses.features.budget.mappers.BudgetMapper;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.features.budget_transfer.dtos.response.BudgetTransferRespDto;
import name.expenses.features.budget_transfer.mappers.BudgetTransferMapper;
import name.expenses.features.budget_transfer.models.BudgetTransfer;
import name.expenses.features.category.dtos.response.CategoryRespDto;
import name.expenses.features.category.mappers.CategoryMapper;
import name.expenses.features.category.models.Category;
import name.expenses.features.category.service.CategoryService;
import name.expenses.features.customer.dao.CustomerDAO;
import name.expenses.features.customer.dtos.request.CustomerReqDto;
import name.expenses.features.customer.dtos.request.CustomerUpdateDto;
import name.expenses.features.customer.dtos.response.CustomerRespDto;
import name.expenses.features.customer.mappers.CustomerMapper;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.features.sub_category.mappers.SubCategoryMapper;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.features.transaction.dtos.response.TransactionRespDto;
import name.expenses.features.transaction.mappers.TransactionMapper;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.features.user.models.User;
import name.expenses.features.user.service.service_impl.AuthService;
import name.expenses.globals.Page;
import name.expenses.globals.PageReq;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class CustomerServiceImpl implements CustomerService {
    public static final String CUSTOMER = "Customer";
    private final CustomerDAO customerDAO;
    private final CustomerMapper customerMapper;
    private final AccountMapper accountMapper;
    private final BudgetMapper budgetMapper;
    private final CategoryMapper categoryMapper;
    private final SubCategoryMapper subCategoryMapper;
    private final TransactionMapper transactionMapper;
    private final BudgetTransferMapper budgetTransferMapper;
    private final ExpenseMapper expenseMapper;
    private final AccountService accountService;
    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final SubService subService;
    private final AuthService authService;
    @Context
    private SecurityContext securityContext;



    @Override
    public ResponseDto create(CustomerReqDto customerReqDto) {
        Customer sentCustomer = customerMapper.reqDtoToEntity(customerReqDto);
        sentCustomer.setAccounts(accountService.getEntities(customerReqDto.getAccountRefs()));
        sentCustomer.setCategories(categoryService.getEntities(customerReqDto.getCategoriesRefs()));
        sentCustomer.setSubCategories(subService.getEntities(customerReqDto.getSubCategoriesRefs()));
        customerReqDto.getUser().setRole("CUSTOMER");
        ResponseDto response = authService.register(customerReqDto.getUser(), true);
        if (!response.isStatus())
            return response;
        sentCustomer.setUser((User) response.getData());
        Account account = accountService.getDefaultAccount();
        Budget budget = budgetService.createDefaultBudget();
        budget.setAccount(account);
        budget.setCustomer(sentCustomer);
        sentCustomer.getAccounts().add(account);
        sentCustomer.getBudgets().add(budget);
        Customer savedCustomer = customerDAO.create(sentCustomer);
        log.info("created customer {}", savedCustomer);
        return ResponseDtoBuilder.getCreateResponse(CUSTOMER, savedCustomer.getUser().getRefNo(), customerMapper.entityToRespDto(savedCustomer));
    }

    public Optional<Customer> getEntity(String refNo){
        try {
            Optional<Customer> customerOptional = customerDAO.get(refNo);
            log.info("fetched customer {}", customerOptional);
            return customerOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<Customer> customerOptional = getEntity(refNo);
            log.info("fetched customer {}", customerOptional);
            if (customerOptional.isPresent()){
                Customer customer = customerOptional.get();
                return ResponseDtoBuilder.getFetchResponse(CUSTOMER, customer.getUser().getRefNo(), customerMapper.entityToRespDto(customer));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("customer with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("customer with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, CustomerUpdateDto customerUpdateDto) {
        Optional<Customer> customerOptional = getEntity(refNo);
        if (customerOptional.isPresent()){
            Customer customer = customerOptional.get();
            log.info("fetched customer {}", customer);
            customerMapper.update(customer, customerUpdateDto);
            log.info("updated customer {}", customer);
            customer.getUser().setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(CUSTOMER, customer.getUser().getRefNo(), customerMapper.entityToRespDto(customerDAO.update(customer)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("customer with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(CUSTOMER,customerDAO.delete(refNo));
    }

    @Override
    public ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Customer> customerPage = customerDAO.findAll(pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<CustomerRespDto> customerDtos = customerMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public Set<Customer> getEntities(Set<String> refNos) {
        return customerDAO.getEntities(refNos);
    }


    @Override
    public void updateAssociation(Customer entity, CustomerUpdateDto entityUpdateDto) {
    }

    @Override
    public Customer update(Customer customer) {
        return customerDAO.update(customer);
    }
    private Customer getCustomer(){
        try {
            return ((User) securityContext.getUserPrincipal()).getCustomer();
        }catch (Exception ex){
            return null;
        }
    }

    private Long getCurrentCustomerId(){
        Customer customer = getCustomer();
        if (customer == null) {
            throw new APIException(ErrorCode.CST_IS_NOT_FOUND.getErrorCode());
        }
        return customer.getId();
    }
    @Override
    public ResponseDto getCustomerAssociation(Models models) {
        Customer customer = getCustomer();
        if (customer == null) {
            throw new APIException(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        }
        Object associations = switch (models){
            case ACCOUNT -> accountMapper.entityToRespDto(customer.getAccounts());
            case SUB_CATEGORY -> subCategoryMapper.entityToRespDto(customer.getSubCategories());
            case EXPENSE -> expenseMapper.entityToRespDto(customer.getExpenses());
            case Budget -> budgetMapper.entityToRespDto(customer.getBudgets());
            case CATEGORY -> categoryMapper.entityToRespDto(customer.getCategories());
            case TRANSACTION -> transactionMapper.entityToRespDto(customer.getTransactions());
            default -> null;
        };
        return ResponseDtoBuilder.getFetchResponse(String.format("customer's %ss", models), customer.getRefNo(), associations);
    }

    @Override
    public CustomerRespDto create(Customer customer) {
        Account account = accountService.getDefaultAccount();
        Budget budget = budgetService.createDefaultBudget();
        budget.setAccount(account);
        budget.setCustomer(customer);
        customer.getAccounts().add(account);
        customer.getBudgets().add(budget);
        return customerMapper.entityToRespDto(customerDAO.create(customer));
    }

    @Override
    public ResponseDto getAllCustomerExpenses(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Expense> customerPage = customerDAO.getAllCustomerExpenses(getCurrentCustomerId(), pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<ExpenseRespDto> customerDtos = expenseMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerSubCategories(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<SubCategory> customerPage = customerDAO.getAllCustomerSubCategories(getCurrentCustomerId(), pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<SubCategoryRespDto> customerDtos = subCategoryMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerBudgets(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Budget> customerPage = customerDAO.getAllCustomerBudgets(getCurrentCustomerId(), pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<BudgetRespDto> customerDtos = budgetMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerCategories(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Category> customerPage = customerDAO.getAllCustomerCategories(getCurrentCustomerId(), pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<CategoryRespDto> customerDtos = categoryMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerAccounts(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Account> customerPage = customerDAO.getAllCustomerAccounts(getCurrentCustomerId(), pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<AccountRespDto> customerDtos = accountMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerBudgetTransfers(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<BudgetTransfer> customerPage = customerDAO.getAllCustomerBudgetTransfers(getCurrentCustomerId(), pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<BudgetTransferRespDto> customerDtos = budgetTransferMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerTransactions(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Transaction> customerPage = customerDAO.getAllCustomerTransactions(getCurrentCustomerId(), pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<TransactionRespDto> customerDtos = transactionMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerSubCategoryExpenses(String subCategoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
                PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Expense> customerPage = customerDAO.getAllCustomerSubCategoryExpenses(getCurrentCustomerId(), subCategoryRef, pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<ExpenseRespDto> customerDtos = expenseMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerCategorySubCategories(String categoryRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
                PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<SubCategory> customerPage = customerDAO.getAllCustomerCategorySubCategories(getCurrentCustomerId(), categoryRef, pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<SubCategoryRespDto> customerDtos = subCategoryMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }

    @Override
    public ResponseDto getAllCustomerAccountBudgets(String accountRef, Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
                PageReq pageReq = ValidateInputUtils.validatePageData(pageNumber, pageSize);
        Page<Budget> customerPage = customerDAO.getAllCustomerAccountBudgets(getCurrentCustomerId(), accountRef, pageReq.pageNumber(), pageReq.pageSize(), sortBy, sortDirection);
        Page<BudgetRespDto> customerDtos = budgetMapper.entityToRespDto(customerPage);
        return ResponseDtoBuilder.getFetchAllResponse(CUSTOMER, customerDtos);
    }
}
