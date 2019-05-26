package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomCustomerRepository {
  List<Customer> findByIdInAndRecordStatusTrue(List<Long> customerIds);

}
