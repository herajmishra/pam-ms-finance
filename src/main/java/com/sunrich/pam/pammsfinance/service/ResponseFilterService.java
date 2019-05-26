package com.sunrich.pam.pammsfinance.service;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ResponseFilterService {
  public FilterProvider getFilterObject(Set<String> filter) {

    return new SimpleFilterProvider().addFilter("customerBankPaymentFilter",
            filter == null ? SimpleBeanPropertyFilter.serializeAll() : SimpleBeanPropertyFilter.filterOutAllExcept(filter));
  }

}
