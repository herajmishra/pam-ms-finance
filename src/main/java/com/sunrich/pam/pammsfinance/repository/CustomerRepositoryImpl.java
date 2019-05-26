package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.Customer;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class CustomerRepositoryImpl implements CustomCustomerRepository {
  @PersistenceContext
  private EntityManager em;

  @Override
  public String findCustomerName(Long id) {
    Session session = (Session) em.getDelegate();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
    Root<Customer> customerRoot = criteriaQuery.from(Customer.class);
    criteriaQuery.select(customerRoot.get("name"));
    criteriaQuery.where(criteriaBuilder.equal(customerRoot.get("id"), id));

    Query<String> query = session.createQuery(criteriaQuery);

    return query.uniqueResult();
  }
}
