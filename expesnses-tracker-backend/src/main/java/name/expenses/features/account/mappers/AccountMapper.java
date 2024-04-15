package name.expenses.features.account.mappers;





import name.expenses.features.account.dtos.request.AccountReqDto;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.dtos.response.AccountRespDto;
import name.expenses.features.account.models.Account;
import name.expenses.features.customer.mappers.CustomerMapper;
import name.expenses.features.pocket.mappers.PocketMapper;
import name.expenses.features.user.models.Role;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
                PocketMapper.class
        },
        imports = {LocalDateTime.class})
public interface AccountMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customers", ignore = true),

            }

    )
    Account reqDtoToEntity(AccountReqDto entityReqDto);
    AccountRespDto entityToRespDto(Account entity);
    Set<AccountRespDto> entityToRespDto(Set<Account> entities);
    List<AccountRespDto> entityToRespDto(List<Account> entities);
    Page<AccountRespDto> entityToRespDto(Page<Account> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "customers", ignore = true),

            }

    )
    void update(@MappingTarget Account entity, AccountUpdateDto entityUpdateDto);
    default List<String> map(Set<Role> value){
        return new ArrayList<>();
    }
}
