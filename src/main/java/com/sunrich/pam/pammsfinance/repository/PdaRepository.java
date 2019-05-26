package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.pda.PdaData;
import com.sunrich.pam.common.dto.pda.PdaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdaRepository extends JpaRepository<PdaData, Long>, PdaRepositoryCustom {

  List<PdaData> findByCustomerAndPdaStatusAndRecordStatusTrue(Long customerId, String acc);

  List<PdaData> findByPortInAndPdaStatusAndRecordStatusTrue(List<Long> portIds, String acc);

  @Query(value = "select v.name as vesselName, b.name as branchName, c.name as customerName, c.address as customerAddress, p.iso_port_code as isoPortCode, p.description as portName," +
          " bd.bank as bankName from pda_data pd inner join vessel v on (pd.vessel = v.id)" +
          " inner join branch b on (pd.branch =  b.id)" +
          " inner join customer c on (pd.customer = c.id)" +
          " inner join port p on (pd.port = p.id)" +
          " inner join bank_details bd on (pd.bank = bd.id)" +
          " WHERE pd.record_status = true AND pd.id =:id", nativeQuery = true)
  PdaProjection findPdaData(Long id);

  @Query(value = "select DISTINCT b.description as berthName from pda_data p inner join berth b on (p.berth = b.id) WHERE p.record_status = true AND p.berth =:berth", nativeQuery = true)
  PdaProjection getBerthName(String berth);
}
