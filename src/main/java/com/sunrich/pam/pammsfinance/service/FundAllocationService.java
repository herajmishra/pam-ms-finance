package com.sunrich.pam.pammsfinance.service;

import com.sunrich.pam.common.domain.finance.BankPayment;
import com.sunrich.pam.common.domain.finance.FundAllocation;
import com.sunrich.pam.common.domain.pda.PdaData;
import com.sunrich.pam.common.dto.finance.BankPaymentDto;
import com.sunrich.pam.common.dto.finance.FundAllocationDto;
import com.sunrich.pam.common.dto.finance.JobDetailsProjection;
import com.sunrich.pam.common.dto.pda.PdaDto;
import com.sunrich.pam.common.exception.ErrorCodes;
import com.sunrich.pam.common.exception.NotFoundException;
import com.sunrich.pam.pammsfinance.repository.BankPaymentRepository;
import com.sunrich.pam.pammsfinance.repository.FundAllocationRepository;
import com.sunrich.pam.pammsfinance.repository.PdaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FundAllocationService {
  private FundAllocationRepository fundAllocationRepository;
  private BankPaymentRepository bankPaymentRepository;
  private PdaRepository pdaRepository;
  private ModelMapper modelMapper;

  public FundAllocationService(FundAllocationRepository fundAllocationRepository, ModelMapper modelMapper, PdaRepository pdaRepository, BankPaymentRepository bankPaymentRepository) {
    this.fundAllocationRepository = fundAllocationRepository;
    this.bankPaymentRepository = bankPaymentRepository;
    this.pdaRepository = pdaRepository;
    this.modelMapper = modelMapper;
  }
  /**
   * Used to save or update the fund allocation details
   *
   * @param payload - domain object
   * @return - the saved/updated fund allocation details object
   */
  public List<FundAllocationDto> saveOrUpdate(List<FundAllocationDto> payload) {

    List<FundAllocationDto> fundAllocationDtoList = new ArrayList<>();
    for (FundAllocationDto fundAllocationDto : payload) {

      FundAllocation fundAllocation = new FundAllocation();
      if (fundAllocationDto.getId() != null) {
        fundAllocation = findEntityById(fundAllocationDto.getId());
      }
      modelMapper.map(fundAllocationDto, fundAllocation);

      fundAllocation.setRecordStatus(true);
      FundAllocation savedFundAllocation = fundAllocationRepository.save(fundAllocation);
      FundAllocationDto fundAllocationDto1 = modelMapper.map(savedFundAllocation, FundAllocationDto.class);
      fundAllocationDtoList.add(fundAllocationDto1);
    }
    return fundAllocationDtoList;
  }
  /**
   * Used to get the fund allocation details by id
   *
   * @param id - fund allocation identifier
   * @return - fundAllocatinDto object
   *
   */
  public List<FundAllocationDto> findAllocatedFundByJobNumber(String id) {
    List<FundAllocation> fundAllocationList = fundAllocationRepository.findByJobNoAndRecordStatusTrue(id);
    List<FundAllocationDto> fundAllocationDtoList = new ArrayList<>();

    if (!fundAllocationList.isEmpty()) {
      for (FundAllocation fundAllocation : fundAllocationList) {
        //to get payment detail by bank payment id and set to fund allocation detail's
        BankPayment bankPayment = bankPaymentRepository.findByIdAndRecordStatusTrue(fundAllocation.getBankPaymentId()).get();
        FundAllocationDto fundAllocationDto = modelMapper.map(fundAllocation, FundAllocationDto.class);
        BankPaymentDto bankPaymentDto = modelMapper.map(bankPayment, BankPaymentDto.class);
        fundAllocationDto.setBankPaymentDto(bankPaymentDto);
        fundAllocationDtoList.add(fundAllocationDto);
      }
    }
    return fundAllocationDtoList;
  }

  private FundAllocation findEntityById(Long id) {
    Optional<FundAllocation> fundAllocationOptional = fundAllocationRepository.findByIdAndRecordStatusTrue(id);
    if (!fundAllocationOptional.isPresent()) {
      throw new NotFoundException(ErrorCodes.FUND_ALLOCATION_NOT_FOUND, "Fund Allocation not found");
    }
    return fundAllocationOptional.get();
  }

  /**
   * Used to get the fund allocation details by job number
   *
   * @param jobNumber - fund allocation identifier
   * @return - allocatedJobDto object
   */
  public PdaDto findJobDetails(String jobNumber) {

    PdaDto jobDetailsDto = new PdaDto();
    JobDetailsProjection jobDetailsProjection = fundAllocationRepository.findAllocationDetails(jobNumber);

    if (jobDetailsProjection != null) {

      jobDetailsDto.setJobNo(jobDetailsProjection.getJobNumber());
      jobDetailsDto.setVessel(jobDetailsProjection.getVessel());
      jobDetailsDto.setVesselName(jobDetailsProjection.getVesselName());
      jobDetailsDto.setVoyage(jobDetailsProjection.getVoyage());
      jobDetailsDto.setEta(jobDetailsProjection.getEta());
      jobDetailsDto.setCustomer(jobDetailsProjection.getCustomer());
      jobDetailsDto.setCustomerName(jobDetailsProjection.getCustomerName());
      jobDetailsDto.setPort(jobDetailsProjection.getPort());
      jobDetailsDto.setPortName(jobDetailsProjection.getPortName());
      jobDetailsDto.setOrgCurrency(jobDetailsProjection.getCurrency());
      jobDetailsDto.setPdaAmount(jobDetailsProjection.getPdaAmount());
      jobDetailsDto.setRoe(jobDetailsProjection.getPdaRoe());

    }
    return jobDetailsDto;
  }

  /**
   * Used to get all list of job for given customer whose pda status is Accepted
   *
   * @param customerId - fund all job identifier
   * @return - List of job's object
   */
  public List<PdaDto> findAllJobByCustomerId(Long customerId) {

    List<PdaDto> listOfJobDetailsDto = new ArrayList<>();
    List<PdaData> pdaDataList = pdaRepository.findByCustomerAndPdaStatusAndRecordStatusTrue(customerId, "ACC");

    for (PdaData pdaData : pdaDataList) {
      PdaDto jobDetailsDto = findJobDetails(pdaData.getJobNo());
      //find pda amount
      JobDetailsProjection jobDetailsProjection = fundAllocationRepository.findPdaAmount(pdaData.getJobNo());
      if (jobDetailsProjection != null) {
        jobDetailsDto.setPdaAmount(jobDetailsProjection.getPdaAmount());
      }
      modelMapper.map(pdaData, jobDetailsDto);
      listOfJobDetailsDto.add(jobDetailsDto);
    }
    return listOfJobDetailsDto;
  }

  public FundAllocationDto approveOrReject(Long id, Boolean isApproved) {
    Optional<FundAllocation> optional = fundAllocationRepository.findByIdAndRecordStatusTrue(id);
    if (!optional.isPresent()) {
      throw new NotFoundException(ErrorCodes.FUND_ALLOCATION_NOT_FOUND, "fund allocation not found");
    }
    FundAllocation fundAllocation = optional.get();
    fundAllocation.setIsApproved(isApproved);
    FundAllocation savedFundAllocation = fundAllocationRepository.save(fundAllocation);
    return modelMapper.map(savedFundAllocation, FundAllocationDto.class);
  }
}
