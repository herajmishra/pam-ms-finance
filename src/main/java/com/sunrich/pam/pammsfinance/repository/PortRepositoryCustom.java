package com.sunrich.pam.pammsfinance.repository;

import java.util.List;

public interface PortRepositoryCustom {
  List<Long> getPortIds(Long branchId);
}
