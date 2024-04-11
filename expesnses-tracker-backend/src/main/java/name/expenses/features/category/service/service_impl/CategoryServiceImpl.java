package name.expenses.features.category.service.service_impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.category.dao.CategoryDAO;
import name.expenses.features.category.dtos.request.CategoryReqDto;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.dtos.response.CategoryRespDto;
import name.expenses.features.category.mappers.CategoryMapper;
import name.expenses.features.category.models.Category;
import name.expenses.features.category.service.CategoryService;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.globals.association.CollectionAssociation;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.category_association_manager.CategoryAssociationManager;
import name.expenses.utils.category_association_manager.UpdateCategoryServiceImpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Transactional
public class CategoryServiceImpl implements CategoryService {
    public static final String CATEGORY = "Category";
    private final CategoryDAO categoryDAO;
    private final CategoryMapper categoryMapper;
    private final CategoryAssociationManager categoryAssociationManager;
    private final UpdateCategoryServiceImpl updateCategoryService;



    @Override
    public ResponseDto create(CategoryReqDto category) {
        Category sentCategory = categoryMapper.reqDtoToEntity(category);
        Category savedCategory = categoryDAO.create(sentCategory);
        log.info("created category {}", savedCategory);
        return ResponseDtoBuilder.getCreateResponse(CATEGORY, savedCategory.getRefNo(), categoryMapper.entityToRespDto(savedCategory));
    }

    public Optional<Category> getEntity(String refNo){
        try {
            Optional<Category> categoryOptional = categoryDAO.get(refNo);
            log.info("fetched category {}", categoryOptional);
            return categoryOptional;

        }catch (Exception ex){
            return Optional.empty();
        }
    }
    @Override
    public ResponseDto get(String refNo) {
        try {
            Optional<Category> categoryOptional = getEntity(refNo);
            log.info("fetched category {}", categoryOptional);
            if (categoryOptional.isPresent()){
                Category category = categoryOptional.get();
                return ResponseDtoBuilder.getFetchResponse(CATEGORY, category.getRefNo(), categoryMapper.entityToRespDto(category));
            }else {
                throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                        Map.of("error", String.format("category with the ref number %s was not found", refNo)));
            }

        }catch (Exception ex){
            ResponseError responseError = new ResponseError();
            responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
            responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
            responseError.setErrorMessage(String.format("category with the ref number %s was not found", refNo));
            return ResponseDtoBuilder.getErrorResponse(804, responseError);
        }

    }

    @Override
    public ResponseDto update(String refNo, CategoryUpdateDto categoryUpdateDto) {
        Optional<Category> categoryOptional = getEntity(refNo);
        if (categoryOptional.isPresent()){
            Category category = categoryOptional.get();
            log.info("fetched category {}", category);
            categoryMapper.update(category, categoryUpdateDto);
            updateCategoryService.updateCategoryAssociations(category, categoryUpdateDto);
            log.info("updated category {}", category);
            category.setUpdatedAt(LocalDateTime.now());
            return ResponseDtoBuilder.getUpdateResponse(CATEGORY, category.getRefNo(), categoryMapper.entityToRespDto(categoryDAO.update(category)));
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("category with the ref number %s was not found", refNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);

    }

    @Override
    public ResponseDto delete(String refNo) {
        return ResponseDtoBuilder.getDeleteResponse(CATEGORY,categoryDAO.delete(refNo));
    }

    @Override
    public ResponseDto addAssociation(String categoryRefNo, String subCategoryRefNo) {
        Optional<Category> categoryOptional = getEntity(categoryRefNo);
        if (categoryOptional.isPresent()){
            Category category = categoryOptional.get();
            if (categoryAssociationManager.addAssociation(category, CollectionAssociation.SUB_CATEGORY, subCategoryRefNo)){
                return ResponseDtoBuilder.getUpdateResponse(CATEGORY, categoryRefNo, categoryMapper.entityToRespDto(category));
            }
            return ResponseDtoBuilder.getErrorResponse(804, "something went wrong couldn't add");

        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("category with the ref number %s was not found", categoryRefNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);
    }

    @Override
    public ResponseDto removeAssociation(String categoryRefNo, String subCategoryRefNo) {
        Optional<Category> categoryOptional = getEntity(categoryRefNo);
        if (categoryOptional.isPresent()){
            Category category = categoryOptional.get();
            if (categoryAssociationManager.removeAssociation(category, CollectionAssociation.SUB_CATEGORY, subCategoryRefNo)){
                return ResponseDtoBuilder.getUpdateResponse(CATEGORY, categoryRefNo, categoryMapper.entityToRespDto(category));
            }
            return ResponseDtoBuilder.getErrorResponse(804, "something went wrong couldn't remove");
        }
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        responseError.setErrorCode(ErrorCode.OBJECT_NOT_FOUND.getErrorCode());
        responseError.setErrorMessage(String.format("category with the ref number %s was not found", categoryRefNo));
        return ResponseDtoBuilder.getErrorResponse(804, responseError);
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
        Page<Category> categoryPage = categoryDAO.findAll(pageNumber, pageSize, sortBy, sortDirection);
        Page<CategoryRespDto> categoryDtos = categoryMapper.entityToRespDto(categoryPage);
        return ResponseDtoBuilder.getFetchAllResponse(CATEGORY, categoryDtos);
    }
}
