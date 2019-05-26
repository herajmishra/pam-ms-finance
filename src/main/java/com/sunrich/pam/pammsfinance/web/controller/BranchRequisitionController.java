package com.sunrich.pam.pammsfinance.web.controller;

import com.sunrich.pam.common.dto.finance.BillDetailsDto;
import com.sunrich.pam.common.dto.finance.BranchRequisitionDto;
import com.sunrich.pam.common.dto.pda.PdaDto;
import com.sunrich.pam.pammsfinance.service.BranchRequisitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/branch-requisition")
public class BranchRequisitionController {
  private BranchRequisitionService branchRequisitionService;

  public BranchRequisitionController(BranchRequisitionService branchRequisitionService) {
    this.branchRequisitionService = branchRequisitionService;
  }

  @GetMapping("/accepted-job-list/{branchId}")
  public ResponseEntity getJobs(@PathVariable Long branchId) {
    List<PdaDto> pdaDtoList = branchRequisitionService.getJobList(branchId);
    return ResponseEntity.ok().body(pdaDtoList);
  }

  @GetMapping("{serviceId}")
  public ResponseEntity findById(@PathVariable Long serviceId) {
    List<BranchRequisitionDto> branchRequisitionDtos = branchRequisitionService.findBranchRequisitionById(serviceId);
    return ResponseEntity.ok().body(branchRequisitionDtos);
  }

  @GetMapping
  public ResponseEntity findAll() {
    List<BranchRequisitionDto> branchRequisitionDtos = branchRequisitionService.findAll();
    return ResponseEntity.ok().body(branchRequisitionDtos);
  }

  @PostMapping
  public ResponseEntity saveOrUpdate(@Valid @RequestBody List<BranchRequisitionDto> payload) {
    List<BranchRequisitionDto> branchRequisitionDto = branchRequisitionService.saveOrUpdate(payload);
    return ResponseEntity.ok().body(branchRequisitionDto);
  }

  @PostMapping("/upload")
  public ResponseEntity uploadFile(@RequestBody List<BillDetailsDto> payload) {

    try {
      branchRequisitionService.saveUploadedFiles(payload);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok().body(null);
  }
}
