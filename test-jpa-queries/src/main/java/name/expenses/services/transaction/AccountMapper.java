package name.expenses.services.transaction;


import name.expenses.dtos.AccountReqDto;
import name.expenses.dtos.AccountRespDto;
import name.expenses.dtos.AccountUpdateDto;
import name.expenses.models.Account;
import name.expenses.models.Models;
import name.expenses.models.Page;
import name.expenses.models.Role;
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
public abstract class AccountMapper {

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customers", ignore = true),
                    @Mapping(target = "pockets", ignore = true),
            }

    )
    public abstract Account reqDtoToEntity(AccountReqDto entityReqDto);
    public abstract AccountRespDto entityToRespDto(Account entity);
    public abstract Set<AccountRespDto> entityToRespDto(Set<Account> entities);
    public abstract List<AccountRespDto> entityToRespDto(List<Account> entities);
    public abstract Page<AccountRespDto> entityToRespDto(Page<Account> entitiesPage);

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
    public abstract void update(@MappingTarget Account entity, AccountUpdateDto entityUpdateDto);
    List<String> map(Set<Role> value){
        return new ArrayList<>();
    }
//    @AfterMapping
//    public void afterMapping(@MappingTarget AccountRespDto.AccountRespDtoBuilder accountRespDtoBuilder, Account account){
//        accountRespDtoBuilder
//                .currentCustomerRegistered(
//                        currentCustomerCollections
//                                .isPresentCollection(account, Models.ACCOUNT));
//    }
}
