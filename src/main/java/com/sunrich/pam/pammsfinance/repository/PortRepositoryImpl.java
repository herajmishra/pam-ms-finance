package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.Port;
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
public class PortRepositoryImpl implements PortRepositoryCustom {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<Long> getPortIds(Long branchId) {
    Session session = (Session) entityManager.getDelegate();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
    Root<Port> portRoot = criteriaQuery.from(Port.class);
    criteriaQuery.select(portRoot.get("id"));
    criteriaQuery.where(criteriaBuilder.equal(portRoot.get("handlingBranch"), branchId),
            criteriaBuilder.equal(portRoot.get("recordStatus"), true));
    Query<Long> query = session.createQuery(criteriaQuery);
    return query.getResultList();
  }

}
