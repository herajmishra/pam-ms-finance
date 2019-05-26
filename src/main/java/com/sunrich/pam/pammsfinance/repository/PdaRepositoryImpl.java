package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.pda.PdaData;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class PdaRepositoryImpl implements PdaRepositoryCustom {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<Long> findCustomerIdByPdaStatus(String status) {
    Session session = (Session) entityManager.getDelegate();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
    Root<PdaData> pdaDataRoot = criteriaQuery.from(PdaData.class);
    criteriaQuery.select(pdaDataRoot.get("customer"));
    criteriaQuery.where(criteriaBuilder.equal(pdaDataRoot.get("pdaStatus"), status));
    criteriaQuery.groupBy(pdaDataRoot.get("customer"));
    Query<Long> query = session.createQuery(criteriaQuery);
    return query.getResultList();
  }
}
