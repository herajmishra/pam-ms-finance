package com.sunrich.pam.pammsfinance.web.controller;

import com.sunrich.pam.common.dto.finance.FundAllocationDto;
import com.sunrich.pam.common.dto.finance.PaymentApprovalDto;
import com.sunrich.pam.common.dto.pda.PdaDto;
import com.sunrich.pam.pammsfinance.service.FundAllocationService;
import com.sunrich.pam.pammsfinance.service.ResponseFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fundAllocation")
public class FundAllocationController {

  private FundAllocationService fundAllocationService;
  private ResponseFilterService responseFilterService;

  public FundAllocationController(FundAllocationService fundAllocationService, ResponseFilterService responseFilterService) {
    this.fundAllocationService = fundAllocationService;
    this.responseFilterService = responseFilterService;
  }

  @GetMapping("{jobNumber}")
  public ResponseEntity findAllocatedFundByJobNumber(@PathVariable String jobNumber) {
    List<FundAllocationDto> fundAllocationDtoList = fundAllocationService.findAllocatedFundByJobNumber(jobNumber);
    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(fundAllocationDtoList);
    mappingJacksonValue.setFilters(responseFilterService.getFilterObject(null));
    return ResponseEntity.ok().body(mappingJacksonValue);
  }

  @PostMapping
  public ResponseEntity saveAllocatedFund(@RequestBody @Valid List<FundAllocationDto> payload) {
    List<FundAllocationDto> dtoList = fundAllocationService.saveOrUpdate(payload);
    return ResponseEntity.ok().body(dtoList);
  }

  @GetMapping("/jobsByCustomer/{customerId}")
  public ResponseEntity findAllJobByCustomerId(@PathVariable Long customerId) {
    List<PdaDto> jobDetailsDtoList = fundAllocationService.findAllJobByCustomerId(customerId);
    return ResponseEntity.ok().body(jobDetailsDtoList);
  }

  @PostMapping("/approval")
  public ResponseEntity<FundAllocationDto> approveOrReject(@Valid @RequestBody PaymentApprovalDto payload) {
    FundAllocationDto fundAllocationDto = fundAllocationService.approveOrReject(payload.getId(), payload.getIsApproved());
    return ResponseEntity.ok().body(fundAllocationDto);
  }
}
