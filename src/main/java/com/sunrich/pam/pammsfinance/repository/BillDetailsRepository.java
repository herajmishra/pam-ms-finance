package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.finance.BillDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillDetailsRepository extends JpaRepository<BillDetails, Long> {
  Optional<BillDetails> findByIdAndRecordStatusTrue(Long id);
}
