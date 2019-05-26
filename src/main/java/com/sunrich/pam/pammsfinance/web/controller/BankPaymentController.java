package com.sunrich.pam.pammsfinance.web.controller;


import com.sunrich.pam.common.dto.CustomerDTO;
import com.sunrich.pam.common.dto.finance.BankPaymentDto;
import com.sunrich.pam.common.dto.finance.PaymentApprovalDto;
import com.sunrich.pam.pammsfinance.service.BankPaymentService;

import com.sunrich.pam.pammsfinance.service.ResponseFilterService;
import com.sunrich.pam.pammsfinance.service.helper.ResponseFilterServiceHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/bankPayment")
public class BankPaymentController {
  private BankPaymentService bankPaymentService;
  private ResponseFilterService responseFilterService;

  public BankPaymentController(BankPaymentService bankPaymentService, ResponseFilterService responseFilterService) {
    this.bankPaymentService = bankPaymentService;
    this.responseFilterService = responseFilterService;
  }

  @GetMapping
  public ResponseEntity findAll() {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(bankPaymentService.findAll());
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @GetMapping("{id}")
  public ResponseEntity findById(@PathVariable Long id) {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(bankPaymentService.findById(id));
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @PostMapping
  public ResponseEntity saveOrUpdate(@Valid @RequestBody List<BankPaymentDto> bankPaymentDtoList) {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(bankPaymentService.saveOrUpdateBulk(bankPaymentDtoList));
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Long bankPaymentId = bankPaymentService.delete(id);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  @GetMapping("/customer")
  public ResponseEntity findCustomer() {
    List<CustomerDTO> customerDTOList = bankPaymentService.findAllCustomer();
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(customerDTOList);
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @GetMapping("/suspense")
  public ResponseEntity findSuspenseBankPayment() {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(bankPaymentService.findSuspenseBankPayment());
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @GetMapping("/getBankReferenceNo")
  public ResponseEntity getBankReferenceNo(@RequestParam Long customerId) {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(bankPaymentService.getBankReferenceNoSuggestion(customerId));
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @PostMapping("/approval")
  public ResponseEntity approveOrReject(@Valid @RequestBody PaymentApprovalDto payload) {
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(bankPaymentService.approveOrReject(payload.getId(), payload.getBankReferenceNo(), payload.getIsApproved()));
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @GetMapping("/getCustomerPaymentList")
  public ResponseEntity getCustomerPaymentList() {
    Set<String> filter = ResponseFilterServiceHelper.getCustomerPaymentFilterList();
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(bankPaymentService.getCustomerPaymentList());
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(filter));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }
}
