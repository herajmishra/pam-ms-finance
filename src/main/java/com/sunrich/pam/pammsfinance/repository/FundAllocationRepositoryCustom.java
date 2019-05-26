package com.sunrich.pam.pammsfinance.repository;

import javax.persistence.Tuple;
import java.util.List;

public interface FundAllocationRepositoryCustom {
  List<Tuple> getBankPaymentIdAndAllocatedAmount();
}
