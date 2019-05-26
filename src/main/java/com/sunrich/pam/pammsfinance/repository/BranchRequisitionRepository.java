package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.finance.BranchRequisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRequisitionRepository extends JpaRepository<BranchRequisition, Long> {

  Optional<BranchRequisition> findByIdNotAndRecordStatusTrue(Long id);

  Optional<BranchRequisition> findByIdAndRecordStatusTrue(Long id);

  List<BranchRequisition> findByServiceIdAndRecordStatusTrue(Long serviceId);

  List<BranchRequisition> findAllByRecordStatusTrueOrderByIdDesc();
}
