/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.common;

import com.unilever.autoclaims.model.enums.FilterTypeEnum;
import com.unilever.autoclaims.model.pojo.SearchParPojo;
import com.unilever.autoclaims.utils.DateUtilities;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Mahmoud.Elsaka
 */
public abstract class AbstractSpecification {

    public static void appendFilterToQuery(Class entityClass, SearchParPojo searchPar, Root root, List<Predicate> predicates, CriteriaBuilder criteriaBuilder) {

        searchPar.getFiltersList().forEach((filter) -> {
            try {

                if (Date.class == getFieldType(entityClass, filter.getFieldName())) {
                    if (FilterTypeEnum.CONTAINS.getFilterType().equals(filter.getType())) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(
                                criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()), criteriaBuilder.literal("yyyy.MM.dd"))
                        ), "%" + filter.getFilter().trim() + "%"));
                    } else if (FilterTypeEnum.EQUALS.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        criteriaBuilder.upper(criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()), criteriaBuilder.literal("yyyy.MM.dd"))),
                                        filter.getFilter().trim()));
                    } else if (FilterTypeEnum.NOT_EQUALS.getFilterType().equals(filter.getType())) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.notEqual(
                                        criteriaBuilder.upper(
                                                criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()), criteriaBuilder.literal("yyyy.MM.dd"))),
                                        filter.getFilter().trim()),
                                criteriaBuilder.isNull(getPathExpression(root, filter.getFieldName()))
                        ));
                    } else if (FilterTypeEnum.STARTS_WITH.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()),
                                                criteriaBuilder.literal("yyyy.MM.dd")),
                                        filter.getFilter().trim() + "%")
                        );
                    } else if (FilterTypeEnum.ENDS_WITH.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()),
                                                criteriaBuilder.literal("yyyy.MM.dd")),
                                        "%" + filter.getFilter().trim())
                        );
                    } else if (FilterTypeEnum.GREATERTHANOREQUAL.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(root.get(filter.getFieldName()).as(java.sql.Date.class), DateUtilities.convertStringToDate("yyyy.MM.dd", filter.getFilter().trim()))
                        );

                    } else if (FilterTypeEnum.LESSTHANOOREQUAL.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(root.get(filter.getFieldName()).as(java.sql.Date.class), DateUtilities.convertStringToDate("yyyy.MM.dd", filter.getFilter().trim()))
                        );

                    }

                } else if (String.class == getFieldType(entityClass, filter.getFieldName())) {
                    if (FilterTypeEnum.CONTAINS.getFilterType().equals(filter.getType())) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(getPathExpression(root, filter.getFieldName())), "%" + filter.getFilter().trim().toUpperCase() + "%"));
                    } else if (FilterTypeEnum.EQUALS.getFilterType().equals(filter.getType())) {
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(getPathExpression(root, filter.getFieldName())), filter.getFilter().trim().toUpperCase()));
                    } else if (FilterTypeEnum.NOT_EQUALS.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.or(
                                        criteriaBuilder.notEqual(criteriaBuilder.upper(getPathExpression(root, filter.getFieldName())), filter.getFilter().trim().toUpperCase()),
                                        criteriaBuilder.isNull(getPathExpression(root, filter.getFieldName()))
                                )
                        );
                    } else if (FilterTypeEnum.STARTS_WITH.getFilterType().equals(filter.getType())) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(getPathExpression(root, filter.getFieldName())), filter.getFilter().trim().toUpperCase() + "%"));
                    } else if (FilterTypeEnum.ENDS_WITH.getFilterType().equals(filter.getType())) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(getPathExpression(root, filter.getFieldName())), "%" + filter.getFilter().trim().toUpperCase()));
                    }
                } else {
                    if (FilterTypeEnum.CONTAINS.getFilterType().equals(filter.getType())) {

                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(
                                criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()), criteriaBuilder.literal("FM9999999999"))
                        ), "%" + filter.getFilter().trim() + "%"));

                    } else if (FilterTypeEnum.EQUALS.getFilterType().equals(filter.getType())) {
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()), criteriaBuilder.literal("FM9999999999"))), filter.getFilter().trim().toUpperCase()));
                    } else if (FilterTypeEnum.NOT_EQUALS.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.or(
                                        criteriaBuilder.notEqual(criteriaBuilder.upper(criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()), criteriaBuilder.literal("FM9999999999"))), filter.getFilter().trim().toUpperCase()),
                                        criteriaBuilder.isNull(getPathExpression(root, filter.getFieldName()))
                                )
                        );
                    } else if (FilterTypeEnum.STARTS_WITH.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()),
                                                criteriaBuilder.literal("FM9999999999")),
                                        filter.getFilter().trim() + "%")
                        );
                    } else if (FilterTypeEnum.ENDS_WITH.getFilterType().equals(filter.getType())) {
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.function("TO_CHAR", String.class, getPathExpression(root, filter.getFieldName()),
                                                criteriaBuilder.literal("FM9999999999")),
                                        "%" + filter.getFilter().trim())
                        );
                    }
                }
            } catch (NoSuchFieldException | SecurityException ex) {
                Logger.getLogger(AbstractSpecification.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    private static Class getFieldType(Class root, String exprString) throws NoSuchFieldException {
        Class fieldClass = root;
        if (!exprString.contains(".")) {

            fieldClass = root.getDeclaredField(exprString).getType();

        } else {
            List<String> rootsList = Arrays.asList(exprString.trim().split(Pattern.quote(".")));

            for (String entityPath : rootsList) {
                fieldClass = fieldClass.getDeclaredField(entityPath).getType();
            }
        }

        return fieldClass;
    }

    public static Expression getPathExpression(Root root, String exprString) {
        Path path = root;

        if (!exprString.contains(".")) {

            path = root.get(exprString);

        } else {
            List<String> rootsList = Arrays.asList(exprString.trim().split(Pattern.quote(".")));

            for (String entityPath : rootsList) {
                path = path.get(entityPath);
            }
        }

        return path;
    }
}
