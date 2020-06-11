/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.controller;

import com.unilever.autoclaims.business.dao.common.GenericEntitySpecfications;
import com.unilever.autoclaims.business.dao.common.GenericJPARepository;
import com.unilever.autoclaims.exceptions.BusinessException;
import com.unilever.autoclaims.model.dto.PageDto;
import com.unilever.autoclaims.model.enums.CrudOpTypesEnum;
import com.unilever.autoclaims.model.enums.ResponseMessageEnum;
import com.unilever.autoclaims.model.enums.SortTypeEnum;
import com.unilever.autoclaims.model.pojo.ResponsePojo;
import com.unilever.autoclaims.model.pojo.SearchParPojo;
import com.unilever.autoclaims.security.utils.JWTUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This Is Base Class for Crud Operation for domain entities
 *
 * @author Mahmoud.Elsaka
 * @param <T>
 */
//http://shrestha-manoj.blogspot.com/2014/05/javaxpersistence-vs-orghibernateannoata.html?_sm_au_=iVVHqGr6R07s3mPN
public class BaseRestController<T extends Serializable> {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private final Logger logger = LoggerFactory.getLogger(BaseRestController.class);
    protected boolean canCreate, canEdit, canDelete, canFind, responseByMobileDto, enableCash;
    ///GenericEntitySpecfications is abstract class if your controller has specification just add speceification class for desired entity
    protected GenericEntitySpecfications<T> genericEntitySpecfications;

    @Autowired
    private GenericJPARepository<T> genericJpARepository;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Initialization">
    ///Using Child  Controllers Class constructor when control crud operations and instantiate specifcations
    public BaseRestController() {
        canCreate = true;
        canEdit = true;
        canDelete = false;
        canFind = true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CRUD Operation">
    @RequestMapping(value = "findListPagesBySpecifications", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity findListPagesBySpecifications(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestBody Optional<SearchParPojo> filterByObj, HttpServletRequest request, HttpServletResponse response) {
        try {
            validateCrudOperation(CrudOpTypesEnum.FIND.getCrudTypeId());
            SearchParPojo searchParPojo = filterByObj.isPresent() ? filterByObj.get() : null;
            Page<T> entityPage = null;
            if (searchParPojo != null) {
                entityPage = findEntityPageBySpecifications(searchParPojo);
            }

            Object pageObject = buildPageDto(entityPage);
            enableResponseCashParatmeter(request, response);
            return findObjectResponseEntity(pageObject, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);

        }

    }

    private PageDto buildPageDto(Page<T> entityPage) {
        PageDto pageDto = new PageDto();
        if (entityPage != null) {
            pageDto.setData(entityPage.getContent());
            pageDto.setTotalElements(entityPage.getTotalElements());
            pageDto.setTotalPages(entityPage.getTotalPages());
        }

        return pageDto;
    }

    @RequestMapping(value = "findOneBySpecifications", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity findOneBySpecifications(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestBody Optional<SearchParPojo> filterByObj) {
        try {
            T entity = null;
            validateCrudOperation(CrudOpTypesEnum.FIND.getCrudTypeId());
            SearchParPojo searchParPojo = filterByObj.isPresent() ? filterByObj.get() : null;
            if (searchParPojo != null) {
                searchParPojo.setPage(0);
                searchParPojo.setSize(1);
            }
            List<T> list = findListPagesBySpceifications(searchParPojo);
            if (list != null && !list.isEmpty()) {
                entity = list.get(0);
                prepareOneObjectData(entity);
            }
            return findObjectResponseEntity(entity, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);

        }

    }

    protected void prepareOneObjectData(T object) {

    }

    private List<T> findListPagesBySpceifications(SearchParPojo searchParPojo) {
        List<T> resultList = null;
        if (searchParPojo != null && searchParPojo.getPage() != null && searchParPojo.getSize() != null) {
            resultList = findAllBySearchParamAndFilterPages(searchParPojo);
        }
        return resultList;

    }

    protected T findOneEntityBySpecifications(Specification specification) {
        T oneEntity = null;
        Optional<T> optional = genericJpARepository.findOne(specification);
        if (optional != null && optional.isPresent()) {
            oneEntity = optional.get();
        }
        return oneEntity;
    }

    @RequestMapping(value = "/findListBySpecifications", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity findListBySpecifications(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestBody Optional<SearchParPojo> filterByObj) {
        try {
            validateCrudOperation(CrudOpTypesEnum.FIND.getCrudTypeId());
            SearchParPojo searchParPojo = filterByObj.isPresent() ? filterByObj.get() : null;
            List<T> resultList = null;
            if (searchParPojo != null) {
                resultList = genericJpARepository.findAll(buildSpecification(searchParPojo));
            }
            return findObjectResponseEntity(resultList, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);
        }
    }

    protected List<T> findAllBySpecification(Specification specification) {
        return genericJpARepository.findAll(specification);
    }

    protected Long countBySpecification(Specification specification) {
        return genericJpARepository.count(specification);
    }

    private Page<T> findEntityPageBySpecifications(SearchParPojo searchParPojo) {
        Page<T> entityPage = null;
        Specification specification = buildSpecification(searchParPojo);
        if (searchParPojo != null && searchParPojo.getPage() != null && searchParPojo.getSize() != null) {
            entityPage = genericJpARepository.findAll(specification, buildPageRequest(searchParPojo));
        }
        return entityPage;
    }

    private PageRequest buildPageRequest(SearchParPojo searchParPojo) {
        int page = 0;
        int size = 1;
        if (searchParPojo != null && searchParPojo.getPage() != null && searchParPojo.getSize() != null) {
            page = searchParPojo.getPage();
            size = searchParPojo.getSize();
        }
        Sort sortBy = buildSortField(searchParPojo);

        return sortBy != null ? PageRequest.of(page, size, sortBy) : PageRequest.of(page, size);
    }

    private Sort buildSortField(SearchParPojo searchParPojo) {
        Sort sortBy = null;
        if (searchParPojo != null && searchParPojo.getSortObject() != null) {
            if (searchParPojo.getSortObject().getFieldName() != null && !searchParPojo.getSortObject().getFieldName().isEmpty()) {
                String sortingField = searchParPojo.getSortObject().getFieldName();

                sortBy = Sort.by(findSortDirection(searchParPojo.getSortObject().getSortDirection()), sortingField);
            }
        }
        return sortBy;

    }

    private Sort.Direction findSortDirection(String sortDir) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir != null && sortDir.equalsIgnoreCase(SortTypeEnum.DESC.getSortType())) {
            direction = Sort.Direction.DESC;
        }
        return direction;
    }

    protected List<T> findAllBySearchParamAndFilterPages(SearchParPojo searchPar) {
        List<T> contentList = null;
        if (searchPar != null) {
            Page<T> contentPages = findEntityPageBySpecifications(searchPar);
            if (contentPages != null) {
                contentList = contentPages.getContent();
            }
        }

        return contentList;

    }

    protected Specification buildSpecification(SearchParPojo searchPar) {
        Specification specification = null;
        if (genericEntitySpecfications != null) {
            specification = genericEntitySpecfications.filterEntityList(searchPar);
        }
        return specification;
    }

    @RequestMapping(value = "findAll")
    public @ResponseBody
    ResponseEntity findAll(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization) throws Exception {
        try {
            validateCrudOperation(CrudOpTypesEnum.FIND.getCrudTypeId());
            Iterable<T> all = this.genericJpARepository.findAll();
            return findObjectResponseEntity(IteratorUtils.toList(all.iterator()), headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);

        }
    }

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity create(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestBody T json) throws Exception {
        try {
            validateCrudOperation(CrudOpTypesEnum.CREATE.getCrudTypeId());
            logger.debug("create() with body {} of type {}", json, json.getClass());
            createEntity(json);
            return buildResponseEntity(true, null, ResponseMessageEnum.SUCCESS.getMessage(), HttpStatus.OK, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);

        }
    }

    private T createEntity(T json) {
        prePareEntityBeforeCreate(json);
        return saveEntity(json);
    }

    @RequestMapping(value = "createAll", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity createAll(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @RequestBody List<T> jsonList) throws Exception {
        try {
            List<T> entityList = null;
            List<T> savedList = null;
            validateCrudOperation(CrudOpTypesEnum.CREATE.getCrudTypeId());
            if (jsonList != null && !jsonList.isEmpty()) {
                entityList = new ArrayList<>();
                for (T jsonElm : jsonList) {
                    prePareEntityBeforeCreate(jsonElm);
                    entityList.add(jsonElm);
                }
                if (!entityList.isEmpty()) {
                    savedList = genericJpARepository.saveAll(entityList);
                }
            }

            return buildResponseEntity(true, null, savedList, HttpStatus.OK, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);

        }
    }

    @RequestMapping(value = "findById/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity findById(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @PathVariable Long id) throws Exception {
        try {
            validateCrudOperation(CrudOpTypesEnum.FIND.getCrudTypeId());
            T entity = findOne(id, null);
            return findObjectResponseEntity(entity, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);

        }
    }

    @RequestMapping(value = "findByCode/{code}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity findByCode(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @PathVariable String code) throws Exception {
        try {
            validateCrudOperation(CrudOpTypesEnum.FIND.getCrudTypeId());
            T entity = findOneByCode(null, code);
            return findObjectResponseEntity(entity, headerAuthorization);
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);
        }
    }

    protected ResponseEntity buildExceptionResponseEntity(Exception exception, String headerAuthorization) {

        String exceptionMessage = exception.getMessage() != null ? exception.getMessage() : "";

        if (exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException) exception;
            if (businessException.getParams() != null && businessException.getParams().length > 0) {
                exceptionMessage = exceptionMessage + "-" + Arrays.toString(businessException.getParams());
            }
        }
        return buildResponseEntity(false, exceptionMessage, null, HttpStatus.OK, headerAuthorization);

    }

    protected ResponseEntity findObjectResponseEntity(T entity, String headerAuthorization) {
        return buildResponseEntity(true, null, entity, HttpStatus.OK, headerAuthorization);
    }

    protected ResponseEntity findObjectResponseEntity(Object object, String headerAuthorization) {
        return buildResponseEntity(true, null, object, HttpStatus.OK, headerAuthorization);

    }

    protected void enableResponseCashParatmeter(HttpServletRequest request, HttpServletResponse response) {
        if (enableCash) {
            response.setHeader("Expires", "300");
            response.setHeader("Cache-Control", "max-age=300, must-revalidate");
            response.setHeader("Pragma", "cache");
        }
    }

    protected ResponseEntity buildResponseEntity(boolean iSuccess, String Message, Object data, HttpStatus httpStatus, String headerAuthorization) {
        ResponsePojo responsePojo = buildResponsePojo(iSuccess, Message, data);
        String responsePojoString = "";
        String signKey = Base64.getEncoder().encodeToString(headerAuthorization.getBytes());
        responsePojoString = JWTUtil.encodeJWT(responsePojo, signKey.getBytes());
        return new ResponseEntity<>(responsePojoString, httpStatus);
    }

    protected ResponsePojo buildResponsePojo(boolean iSuccess, String Message, Object data) {
        ResponsePojo responsePojo = new ResponsePojo(iSuccess, Message, data);
        return responsePojo;
    }

    //https://stackoverflow.com/questions/24482117/when-use-getone-and-findone-methods-spring-data-jpa
    protected T findOne(String id, String code) {
        T entity = null;
        Serializable identifer = id != null ? id : code;
        Optional<T> optional = this.genericJpARepository.findById(identifer);
        if (optional != null && optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    protected T findOne(Long id, String code) {
        T entity = null;
        Serializable identifer = id != null ? id : code;
        Optional<T> optional = this.genericJpARepository.findById(identifer);
        if (optional != null && optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    protected T findOneByCode(Long id, String code) {
        T entity = null;
        Serializable identifer = id != null ? id : code;
        Optional<T> optional = this.genericJpARepository.findById(identifer);
        if (optional != null && optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity update(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @PathVariable String id, @RequestBody T json) throws Exception {
        validateCrudOperation(CrudOpTypesEnum.EDIT.getCrudTypeId());
        logger.debug("update() of id#{} with body {}", id, json);
        logger.debug("T json is of type {}", json.getClass());
        return updateEntity(id, json, headerAuthorization);

    }

    protected T updateEntity(String id, T json) {
        T entity = findOne(id, null);
        T updated = null;
        if (entity != null) {
            try {
                copyProperties(json, entity);
            } catch (Exception e) {
                logger.warn("while copying properties", e);
                throw e;

            }

            logger.debug("merged entity: {}", entity);
            prePareEntityBeforeEdit(entity, json);
            updated = saveEntity(entity);
            logger.debug("updated enitity: {}", updated);
            return updated;
        } else {
            return null;
        }
    }

    protected ResponseEntity updateEntity(String id, T json, String headerAuthorization) {
        try {
            T updated = updateEntity(id, json);
            if (updated != null) {
                return buildResponseEntity(true, null, updated, HttpStatus.OK, headerAuthorization);
            } else {
                logger.debug("non exist enitity: {}", id);
                return buildExceptionResponseEntity(new BusinessException("not found", new String[]{id.toString()}), headerAuthorization);

            }
        } catch (Exception exception) {
            return buildExceptionResponseEntity(exception, headerAuthorization);

        }
    }

    protected void copyProperties(T json, T entity) {
        BeanUtils.copyProperties(json, entity);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity delete(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String headerAuthorization,
            @PathVariable Long id) throws Exception {
        try {
            validateCrudOperation(CrudOpTypesEnum.DELETE.getCrudTypeId());
            if (genericJpARepository.existsById(id)) {
                genericJpARepository.deleteById(id);
                logger.debug("Deleted enitity: {}", id);
                return buildResponseEntity(true, null, id, HttpStatus.OK, headerAuthorization);
            } else {
                return buildExceptionResponseEntity(new BusinessException("not found", new String[]{id.toString()}), headerAuthorization);
            }
        } catch (Exception exception) {
            return buildResponseEntity(false, exception.getMessage(), null, HttpStatus.NOT_ACCEPTABLE, headerAuthorization);

        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Validate Crud Operation">
    protected void findOnlyService() {
        canCreate = false;
        canEdit = false;
        canDelete = false;
    }

    protected boolean canCreate() {
        return canCreate;
    }

    protected boolean canEdit() {
        return canEdit;
    }

    protected boolean canDelete() {
        return canDelete;
    }

    protected boolean canFind() {
        return canFind;
    }

    private void validateCrudOperation(int crudOpeType) throws Exception {
        String validtionError = null;
        if (crudOpeType == CrudOpTypesEnum.CREATE.getCrudTypeId() && !canCreate) {
            validtionError = "CREATION_NOT_ALLOWED";
        } else if (crudOpeType == CrudOpTypesEnum.EDIT.getCrudTypeId() && !canEdit) {
            validtionError = "EDIT_NOT_ALLOWED";
        } else if (crudOpeType == CrudOpTypesEnum.DELETE.getCrudTypeId() && !canDelete) {
            validtionError = "DELETE_NOT_ALLOWED";
        } else if (crudOpeType == CrudOpTypesEnum.FIND.getCrudTypeId() && !canFind) {
            validtionError = "SEARCH_NOT_ALLOWED";
        }

        if (validtionError != null) {
            throw new BusinessException(validtionError);

        }
    }

    protected T saveEntity(T entity) {
        return genericJpARepository.save(entity);

    }

    protected void prePareEntityBeforeCreate(T entity) {
    }

    protected void prePareEntityBeforeEdit(T oldEntity, T newEntity) {
    }
    //</editor-fold>
}
