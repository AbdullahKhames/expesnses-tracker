package name.expenses.features.sub_category.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.models.Category;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.sub_category.dao.SubCategoryDAO;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.features.sub_category.mappers.SubCategoryMapper;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.features.sub_category.service.SubService;
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
@Transactional
public class SubServiceImpl implements SubService {
    public static final String SUBCATEGORY = "SubCategory";
    private final SubCategoryDAO subCategoryDAO;
    private final SubCategoryMapper subCategoryMapper;
    private final ExpenseService expenseService;


    @Override
    public ResponseDto create(SubCategoryReqDto subCategory) {
        SubCategory sentSubCategory = subCategoryMapper.reqDtoToEntity(subCategory);
        SubCategory savedSubCategory = subCategoryDAO.create(sentSubCategory);
        log.info("created subCategory {}", savedSubCategory);
        return ResponseDtoBuilder.getCreateResponse(SUBCATEGORY, savedSubCategory.getRefNo(), subCategoryMapper.entityToRespDto(savedSubCategory));
    }

    public Optional<SubCategory> getEntity(String refNo) {
        try {
            Optional<SubCategory> subCategoryOptional = subCategoryDAO.get(refNo);
            log.info("fetched subCategory {}", subCategoryOptional);
            return subCategoryOptional;

        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<SubCategory> subCategoryOptional = getEntity(refNo);
            log.info("fetched subCategory {}", subCategoryOptional);
            if (subCategoryOptional.isPresent()) {
                SubCategory subCategory = subCategoryOptional.get();
                return ResponseDtoBuilder.getFetchResponse(SUBCATEGORY, subCategory.getRefNo(), subCategoryMapper.entityToRespDto(subCategory));
            } else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("sub category with the ref number %s was not found", refNo)));
            }

        } catch (Exception ex) {
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("sub category with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, SubCategoryUpdateDto subCategoryUpdateDto) {
        Optional<SubCategory> subCategoryOptional = getEntity(refNo);
        if (subCategoryOptional.isPresent()) {
            SubCategory subCategory = subCategoryOptional.get();
            log.info("fetched subCategory {}", subCategory);
            subCategoryMapper.update(subCategory, subCategoryUpdateDto);
            expenseService.updateExpensesAssociation(subCategory, subCategoryUpdateDto);
            log.info("updated subCategory {}", subCategory);
            subCategory.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(SUBCATEGORY, subCategory.getRefNo(), subCategoryMapper.entityToRespDto(subCategoryDAO.update(subCategory)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("sub category with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(SUBCATEGORY, subCategoryDAO.delete(refNo));
    }

    @Override
    public ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        if (pageNumber < 1) {
            pageNumber = 1L;
        }
        if (pageSize < 1) {
            pageSize = 1L;
        }
        Page<SubCategory> subCategoryPage = subCategoryDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);
        Page<SubCategoryRespDto> subCategoryDtos = subCategoryMapper.entityToRespDto(subCategoryPage);
        return ResponseDtoBuilder.getFetchAllResponse(SUBCATEGORY, subCategoryDtos);
    }

    @Override
    public boolean addAssociation(Category category, String refNo) {
        if (ValidateInputUtils.isValidInput(category, category.getSubCategories())) {
            Optional<SubCategory> subCategoryOptional = getEntity(refNo);
            if (subCategoryOptional.isEmpty()) {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("the subcategory with given reference number : %s doesn't exist", refNo)));
            }
            SubCategory subCategory = subCategoryOptional.get();
            Long categoryId = subCategoryDAO.checkCategoryAssociation(subCategory);
            if (categoryId != null) {
                if (Objects.equals(categoryId, category.getId())) {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this subcategory is already present in the given category!!"));
                }else {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this subcategory is already present in another category!!"));
                }
            }

            category.getSubCategories().add(subCategoryOptional.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAssociation(Category category, String refNo) {
        if (ValidateInputUtils.isValidInput(category, category.getSubCategories())) {
            Optional<SubCategory> subCategoryOptional = getEntity(refNo);
            if (subCategoryOptional.isEmpty()) {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("the subcategory with given reference number : %s doesn't exist", refNo)));
            }
            SubCategory subCategory = subCategoryOptional.get();
            Long categoryId = subCategoryDAO.checkCategoryAssociation(subCategory);
            if (categoryId != null) {
                if (Objects.equals(categoryId, category.getId())) {
                    category.getSubCategories().remove(subCategoryOptional.get());
                }else {
                    throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                            Map.of("error", "this subcategory is already present in another category!!"));
                }
            }else {
                throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                        Map.of("error", "this subcategory is bot present in any category!!"));
            }
            return true;
        }
        return false;
    }

    @Override
    public void updateAssociation(Category category, CategoryUpdateDto categoryUpdateDto) {
        if (category.getSubCategories() != null && categoryUpdateDto.getSubCategories() != null) {
            Set<SubCategory> existingSubCategories = category.getSubCategories();
            Set<SubCategoryUpdateDto> subCategoryUpdateDtos = categoryUpdateDto.getSubCategories();
            //new subCategory will contain only the new subCategory ie the one not included with refs
            Set<SubCategory> newSubCategories = getSubCategories(subCategoryUpdateDtos, existingSubCategories);
            //remove non existent in the new collection
            Set<SubCategory> removedSubCategories = new HashSet<>(existingSubCategories);
            List<String> newListRefs = subCategoryUpdateDtos.stream().map(SubCategoryUpdateDto::getRefNo).toList();
            removedSubCategories.removeIf(subCategory -> newListRefs.contains(subCategory.getRefNo()));
            category.getSubCategories().removeAll(removedSubCategories);
            category.getSubCategories().addAll(newSubCategories);
        }

    }
    private Set<SubCategory> getSubCategories (Set<SubCategoryUpdateDto> subCategoryUpdateDtos, Set < SubCategory > existingSubCategories){
        Set<SubCategory> addedSubCategories = new HashSet<>();
        subCategoryUpdateDtos.forEach(newSubCategory -> {
            if (newSubCategory.getRefNo() != null) {
                SubCategory existingSubCategory = findExistingEntity(existingSubCategories, newSubCategory.getRefNo());
                if (existingSubCategory != null) {
                    subCategoryMapper.update(existingSubCategory, newSubCategory);
                    existingSubCategory.setUpdatedAt(LocalDateTime.now());
                    expenseService.updateExpensesAssociation(existingSubCategory, newSubCategory);
                }
            } else {
                //if id doesn't have id add it
                addedSubCategories.add(subCategoryMapper.reqEntityToEntity(newSubCategory));
            }
        });
        return addedSubCategories;
    }

    private SubCategory findExistingEntity (Set < SubCategory > existingSubCategories, String refNo){
        return existingSubCategories.stream().filter(subCategory -> subCategory.getRefNo().equals(refNo)).findFirst().orElse(null);
    }
}
