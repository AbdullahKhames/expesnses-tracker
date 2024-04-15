package name.expenses.features.customer.mappers;





import name.expenses.features.account.mappers.AccountMapper;
import name.expenses.features.category.mappers.CategoryMapper;
import name.expenses.features.customer.dtos.request.CustomerReqDto;
import name.expenses.features.customer.dtos.request.CustomerUpdateDto;
import name.expenses.features.customer.dtos.response.CustomerRespDto;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.pocket.mappers.PocketMapper;
import name.expenses.features.sub_category.mappers.SubCategoryMapper;
import name.expenses.features.user.mappers.UserMapper;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                UserMapper.class,
                AccountMapper.class,
                PocketMapper.class,
                CategoryMapper.class,
                SubCategoryMapper.class,
                ExpenseMapper.class
        },
        imports = {LocalDateTime.class})
public interface CustomerMapper {

    Customer reqDtoToEntity(CustomerReqDto entityReqDto);
    CustomerRespDto entityToRespDto(Customer entity);
    Set<CustomerRespDto> entityToRespDto(Set<Customer> entities);
    List<CustomerRespDto> entityToRespDto(List<Customer> entities);
    Page<CustomerRespDto> entityToRespDto(Page<Customer> entitiesPage);


    void update(@MappingTarget Customer entity, CustomerUpdateDto entityUpdateDto);

    Customer reqEntityToEntity(CustomerUpdateDto newPocket);
}
