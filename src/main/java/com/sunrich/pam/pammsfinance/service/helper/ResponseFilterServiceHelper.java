package com.sunrich.pam.pammsfinance.service.helper;

import java.util.HashSet;
import java.util.Set;

public class ResponseFilterServiceHelper {
  public static Set<String> getCustomerPaymentFilterList() {
    Set<String> filter = new HashSet<>();
    filter.add("customerId");
    filter.add("customerName");
    filter.add("remitter");
    filter.add("currency");
    filter.add("amountReceived");
    filter.add("roe");
    filter.add("netAmountReceivedLocal");
    return filter;
  }
}
