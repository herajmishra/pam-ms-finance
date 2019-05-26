package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.finance.FundAllocation;
import com.sunrich.pam.common.dto.finance.JobDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundAllocationRepository extends JpaRepository<FundAllocation, Long>, FundAllocationRepositoryCustom {

  Optional<FundAllocation> findByIdAndRecordStatusTrue(Long id);
  List<FundAllocation> findByJobNoAndRecordStatusTrue(String jobNumber);

  @Query(value = "select pd.id, pd.job_no as jobNumber, pd.vessel, v.name as vesselName, pd.voyage, pd.eta, pd.customer as customerId, c.name as customerName, pd.port, p.description as portName, pd.org_currency as currency, pd.roe as pdaRoe from pda_data pd " +
          "inner join customer c on (pd.customer = c.id) " +
          "inner join vessel v on( pd.vessel = v.id) " +
          "inner join port p on (pd.port = p.id) WHERE pd.record_status = true AND pd.job_no =:jobNumber", nativeQuery = true)
  JobDetailsProjection findAllocationDetails(String jobNumber);

  @Query(value = "select sum(ps.total_amount) as pdaAmount from pda_data pd " +
          "inner join pda_services ps on( ps.pda_id = pd.id) WHERE pd.record_status = true AND pd.job_no =:jobNumber", nativeQuery = true)
  JobDetailsProjection findPdaAmount(String jobNumber);
}
