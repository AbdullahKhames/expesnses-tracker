package name.expenses.features.association;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.ResponseExceptionBuilder;
import name.expenses.features.account.service.AccountService;
import name.expenses.features.category.service.CategoryService;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.features.user.models.User;
import name.expenses.globals.CrudService;
import name.expenses.globals.responses.ResponseDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
@Singleton
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AssociationManager {
    private final PocketService pocketService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final SubService subService;
    private final ExpenseService expenseService;
    private final CustomerService customerService;
    private final ResponseExceptionBuilder responseExceptionBuilder;
    @Context
    private SecurityContext securityContext;
    @PersistenceContext
    private EntityManager entityManager;

//    private final Map<Models, CollectionAdder<?, ?>> adderHandler = new HashMap<>(
//            Map.of(
//                    Models.POCKET, pocketService,
////                    Models.ACCOUNT, accountService,
//                    Models.SUB_CATEGORY, subService,
////                    Models.CATEGORY, categoryService,
////                    Models.EXPENSE, expenseService,
////                    Models.CUSTOMER, customerService
//            ));
//
//    private final Map<Models, CollectionRemover<?, ?>> removerHandler = new HashMap<>(
//            Map.of(
//                    Models.POCKET, pocketService,
////                    Models.ACCOUNT, accountService,
//                    Models.SUB_CATEGORY, subService,
////                    Models.CATEGORY, categoryService,
////                    Models.EXPENSE, expenseService,
////                    Models.CUSTOMER, customerService
//            ));

    private final Map<Models, CollectionAdder<?>> adderHandler = new HashMap<>();
    private final Map<Models, CollectionRemover<?>> removerHandler = new HashMap<>();

    @PostConstruct
    public void init(){
        adderHandler.put(Models.POCKET, pocketService);
        adderHandler.put(Models.SUB_CATEGORY, subService);
        adderHandler.put(Models.ACCOUNT, accountService);
        adderHandler.put(Models.CATEGORY, categoryService);
        adderHandler.put(Models.EXPENSE, expenseService);


        removerHandler.put(Models.POCKET, pocketService);
        removerHandler.put(Models.SUB_CATEGORY, subService);
        removerHandler.put(Models.ACCOUNT, accountService);
        removerHandler.put(Models.CATEGORY, categoryService);
        removerHandler.put(Models.EXPENSE, expenseService);
    }

    public  CollectionAdder<?> getCollectionAdder(Models models){
        return adderHandler.get(models);
    }

    public CollectionRemover<?> getCollectionRemover(Models models){
        return removerHandler.get(models);
    }

    public CrudService getService(Models models){
        return switch (models) {
            case POCKET -> pocketService;
            case ACCOUNT -> accountService;
            case EXPENSE -> expenseService;
            case CATEGORY -> categoryService;
            case CUSTOMER -> customerService;
            case SUB_CATEGORY -> subService;
            default -> null;
        };
    }

    public ResponseDto addAssociation(String entityRefNo,
                                      Models entityModels,
                                      Set<String> associationRefNos,
            /*Set<?> associationUpdateDto,*/
                                      Models associationModels){
        try{

            Optional<?> optional = getOptionalObject(entityRefNo, entityModels);
            System.out.println(optional);
            if (optional.isPresent()){
                CollectionAdder<?> collectionAdder = getCollectionAdder(associationModels);
                Object entity = optional.get();
                System.out.println(entity);
                System.out.println(collectionAdder);
                ResponseDto responseDto = collectionAdder.addAssociation(entity, entityModels, associationRefNos);
                CrudService crudService = getService(entityModels);
                crudService.update(entity, entityManager);
                return responseDto;
            } else {
                throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                        Map.of("error", "object not found"));
            }
        }catch (Exception exception){
            return responseExceptionBuilder.buildResponse(exception);
        }
    }

    private Optional<?> getOptionalObject(String entityRefNo, Models entityModels) {
        if (entityRefNo == null && entityModels == Models.CUSTOMER){
            entityRefNo = ((User)securityContext.getUserPrincipal()).getRefNo();
        }
        CrudService crudService = getService(entityModels);
        System.out.println(crudService);
        if (crudService == null) {
            throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                    Map.of("error", "service not found for given enum"));
        }
        return crudService.getEntity(entityRefNo);
    }

    public ResponseDto removeAssociation(String entityRefNo, Models entityModels, Set<String> associationRefNos, /*Set<?> associationUpdateDto,*/ Models associationModels){
        try{
            Optional<?> optional = getOptionalObject(entityRefNo, entityModels);
            System.out.println(optional);
            if (optional.isPresent()){
                CollectionRemover<?> collectionRemover = getCollectionRemover(associationModels);
                Object entity = optional.get();
                System.out.println(entity);
                System.out.println(collectionRemover);
                ResponseDto responseDto = collectionRemover.removeAssociation(entity, entityModels, associationRefNos);
                CrudService crudService = getService(entityModels);
                crudService.update(entity, entityManager);
                return responseDto;
            } else {
                throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                        Map.of("error", "object not found"));
            }
        }catch (Exception exception){
            return responseExceptionBuilder.buildResponse(exception);
        }
    }

    public ResponseDto addAssociationDtos(String entityRefNo, Models entityModels, Set<?> associationReqDtos, Models associationModels) {
        try{

            Optional<?> optional = getOptionalObject(entityRefNo, entityModels);
            System.out.println(optional);
            if (optional.isPresent()){
                CollectionAdder<?> collectionAdder = getCollectionAdder(associationModels);
                Object entity = optional.get();
                System.out.println(entity);
                System.out.println(collectionAdder);
                ResponseDto responseDto = collectionAdder.addDtoAssociation(entity, entityModels, associationReqDtos);
                CrudService crudService = getService(entityModels);
                crudService.update(entity, entityManager);
                return responseDto;
            } else {
                throw new GeneralFailureException(GeneralFailureException.GENERAL_ERROR,
                        Map.of("error", "object not found"));
            }
        }catch (Exception exception){
            return responseExceptionBuilder.buildResponse(exception);
        }
    }
}
