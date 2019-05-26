package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.finance.DocumentStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentStorageRepository extends JpaRepository<DocumentStorage, Long> {

  DocumentStorage findByIdAndRecordStatusTrue(Long id);
}
