/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.business.dao.specifications;

import com.unilever.autoclaims.business.dao.common.GenericEntitySpecfications;
import com.unilever.autoclaims.model.ConstantStrings;
import com.unilever.autoclaims.model.entities.U2k2ClaimsData;
import com.unilever.autoclaims.model.entities.U2k2ClaimsDataPK_;
import com.unilever.autoclaims.model.entities.U2k2ClaimsData_;
import com.unilever.autoclaims.utils.DateUtilities;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Mahmoud.Elsaka
 */
public class U2K2ClaimsDataSpecifications extends GenericEntitySpecfications<U2k2ClaimsData> {

    public Specification buildU2K2ClaimsDataSpecifications(List<String> branchNo, Date period, String claimType, boolean checkClaims) {
        return (Specification) (Root root, CriteriaQuery cq, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (branchNo != null && !branchNo.isEmpty()) {
                Expression<String> branchExpression = root.get(U2k2ClaimsData_.distno);
                predicates.add(branchExpression.in(branchNo) );
            }
            if (period != null) {
                predicates.add(cb.equal(root.get(U2k2ClaimsData_.period), DateUtilities.convertDateToString(ConstantStrings.DATE_FORMATE, period)));
            }

            if (claimType != null && !claimType.isEmpty()) {
                String discountType = ConstantStrings.OTHER_DISCOUNT;
                if (claimType.equals(ConstantStrings.AUTO_CLAIMS)) {
                    predicates.add(cb.notEqual(root.get(U2k2ClaimsData_.discounttype), ConstantStrings.OTHER_DISCOUNT));
                } else if (claimType.equals(ConstantStrings.MANUAL_CLAIMS)) {
                    predicates.add(cb.equal(root.get(U2k2ClaimsData_.discounttype), ConstantStrings.OTHER_DISCOUNT));
                }
            }

            //Verify Claims Data Function
            if (checkClaims) {
                predicates.add(cb.or(
                        cb.equal(root.get(U2k2ClaimsData_.internalOrder), "Manual"),
                        cb.equal(root.get(U2k2ClaimsData_.internalOrder), root.get(U2k2ClaimsData_.itemno)),
                        cb.isNull(root.get(U2k2ClaimsData_.internalOrder))));

            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
