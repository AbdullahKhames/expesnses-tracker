package name.expenses.features.customer.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.account.service.AccountService;
import name.expenses.features.category.service.CategoryService;
import name.expenses.features.customer.dao.CustomerDAO;
import name.expenses.features.customer.dtos.request.CustomerReqDto;
import name.expenses.features.customer.dtos.request.CustomerUpdateDto;
import name.expenses.features.customer.dtos.response.CustomerRespDto;
import name.expenses.features.customer.mappers.CustomerMapper;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.features.user.models.User;
import name.expenses.features.user.service.UserService;
import name.expenses.features.user.service.service_impl.AuthService;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.ValidateInputUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
//@Transactional
public class CustomerServiceImpl implements CustomerService {
    public static final String CUSTOMER = "Customer";
    private final CustomerDAO customerDAO;
    private final CustomerMapper customerMapper;
    private final AccountService accountService;
    private final PocketService pocketService;
    private final CategoryService categoryService;
    private final SubService subService;
    private final ExpenseService expenseService;
    private final AuthService authService;



    @Override
    public ResponseDto create(CustomerReqDto customerReqDto) {
        Customer sentCustomer = customerMapper.reqDtoToEntity(customerReqDto);
        sentCustomer.setAccounts(accountService.getEntities(customerReqDto.getAccountRefs()));
        sentCustomer.setCategories(categoryService.getEntities(customerReqDto.getCategoriesRefs()));
        sentCustomer.setSubCategories(subService.getEntities(customerReqDto.getSubCategoriesRefs()));
        customerReqDto.getUser().setRole("CUSTOMER");
        ResponseDto response = authService.register(customerReqDto.getUser());
        if (!response.isStatus())
            return response;
        sentCustomer.setUser((User) response.getData());
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
        if (pageNumber < 1){
            pageNumber = 1L;
        }
        if (pageSize < 1)
        {
            pageSize = 1L;
        }
        Page<Customer> customerPage = customerDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);
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
}
