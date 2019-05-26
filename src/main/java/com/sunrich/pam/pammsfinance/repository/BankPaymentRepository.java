package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.finance.BankPayment;
import com.sunrich.pam.common.dto.finance.BankPaymentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankPaymentRepository extends JpaRepository<BankPayment, Long> {
  List<BankPayment> findAllByRecordStatusTrue();

  Optional<BankPayment> findByIdAndRecordStatusTrue(Long id);

  List<BankPayment> findByCustomerIdAndRecordStatusTrue(Long id);

  @Query(value = "SELECT bd.bank AS bankName FROM bank_payment bp INNER JOIN bank_details bd ON (bp.bank_id = bd.id) WHERE bp.id = :bankPaymentId", nativeQuery = true)
  BankPaymentProjection findBankName(@Param("bankPaymentId") Long id);

  List<BankPayment> findByCustomerIdNullAndRecordStatusTrue();

  List<BankPayment> findByCustomerIdAndRecordStatusTrueAndIsApprovedTrue(Long customerId);

  Optional<BankPayment> findByIdAndBankReferenceNoAndRecordStatusTrue(Long id, String bankReferenceNo);

  @Query(value = "SELECT * FROM bank_payment WHERE customer_id IN (SELECT DISTINCT customer FROM pda_data WHERE pda_status = 'ACC') AND is_approved = TRUE", nativeQuery = true)
  List<BankPayment> findAllCustomerPaymentList();
}
