package com.sunrich.pam.pammsfinance.repository;

import java.util.List;

public interface PdaRepositoryCustom {
  List<Long> findCustomerIdByPdaStatus(String status);
}
