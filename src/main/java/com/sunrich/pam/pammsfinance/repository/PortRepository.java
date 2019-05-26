package com.sunrich.pam.pammsfinance.repository;

import com.sunrich.pam.common.domain.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepository extends JpaRepository<Port, Long>, PortRepositoryCustom {
}
