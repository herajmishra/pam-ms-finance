package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.finance.FundAllocation;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class FundAllocationRepositoryImpl implements FundAllocationRepositoryCustom {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<Tuple> getBankPaymentIdAndAllocatedAmount() {
    Session session = (Session) entityManager.getDelegate();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery criteriaQuery = criteriaBuilder.createTupleQuery();
    Root<FundAllocation> fundAllocationRoot = criteriaQuery.from(FundAllocation.class);
    criteriaQuery.multiselect(fundAllocationRoot.get("bankPaymentId").alias("bankPaymentId"), criteriaBuilder.sum(fundAllocationRoot.get("allocatedFund")).alias("allocatedFund"));
    criteriaQuery.where(criteriaBuilder.equal(fundAllocationRoot.get("recordStatus"), true),
            criteriaBuilder.or(criteriaBuilder.notEqual(fundAllocationRoot.get("isApproved"), false),
                    criteriaBuilder.or(criteriaBuilder.isNull(fundAllocationRoot.get("isApproved")))));
    criteriaQuery.groupBy(fundAllocationRoot.get("bankPaymentId"));

    Query query = session.createQuery(criteriaQuery);
    return query.getResultList();
  }
}
