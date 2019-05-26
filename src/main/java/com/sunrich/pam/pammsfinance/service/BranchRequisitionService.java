package com.sunrich.pam.pammsfinance.service;

import com.sunrich.pam.common.domain.finance.BillDetails;
import com.sunrich.pam.common.domain.finance.BranchRequisition;
import com.sunrich.pam.common.domain.finance.DocumentStorage;
import com.sunrich.pam.common.domain.pda.PdaData;
import com.sunrich.pam.common.dto.finance.BillDetailsDto;
import com.sunrich.pam.common.dto.finance.BranchRequisitionDto;
import com.sunrich.pam.common.dto.pda.PdaDto;
import com.sunrich.pam.common.dto.pda.PdaProjection;
import com.sunrich.pam.common.enums.PdaStatus;
import com.sunrich.pam.common.exception.ErrorCodes;
import com.sunrich.pam.common.exception.NotFoundException;
import com.sunrich.pam.pammsfinance.repository.BillDetailsRepository;
import com.sunrich.pam.pammsfinance.repository.BranchRequisitionRepository;
import com.sunrich.pam.pammsfinance.repository.DocumentStorageRepository;
import com.sunrich.pam.pammsfinance.repository.PdaRepository;
import com.sunrich.pam.pammsfinance.repository.PortRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchRequisitionService {
  private final BranchRequisitionRepository branchRequisitionRepository;
  private PortRepository portRepository;
  private PdaRepository pdaRepository;
  private BillDetailsRepository billDetailsRepository;
  private DocumentStorageRepository documentStorageRepository;
  private ModelMapper modelMapper;

  public BranchRequisitionService(BranchRequisitionRepository branchRequisitionRepository, PortRepository portRepository, PdaRepository pdaRepository, ModelMapper modelMapper) {
    this.branchRequisitionRepository = branchRequisitionRepository;
    this.portRepository = portRepository;
    this.pdaRepository = pdaRepository;
    this.modelMapper = modelMapper;
  }

  public List<PdaDto> getJobList(Long branchId) {
    List<Long> portIds = portRepository.getPortIds(branchId);
    List<PdaData> pdaDataList = pdaRepository.findByPortInAndPdaStatusAndRecordStatusTrue(portIds, PdaStatus.ACC.name());
    List<PdaDto> pdaDtoList = new ArrayList<>();
    if (pdaDataList.isEmpty()) {
      return pdaDtoList;
    }
    for (PdaData pdaData : pdaDataList) {
      PdaDto pdaDto = modelMapper.map(pdaData, PdaDto.class);
      PdaProjection pdaProjection = pdaRepository.findPdaData(pdaDto.getId());
      pdaDto.setVesselName(pdaProjection.getVesselName());
      pdaDto.setBankName(pdaProjection.getBankName());
      pdaDto.setBranchName(pdaProjection.getBranchName());
      pdaDto.setCustomerAddress(pdaProjection.getCustomerAddress());
      pdaDto.setCustomerName(pdaProjection.getCustomerName());
      pdaDto.setPortName(pdaProjection.getPortName());
      if (pdaDto.getBerth() != null) {
        pdaDto.setBerthName(pdaRepository.getBerthName(pdaDto.getBerth()).getBerthName());
      } else {
        pdaDto.setBerthName("NA");
      }
      pdaDtoList.add(pdaDto);
    }
    return pdaDtoList;
  }

  /**
   * Used to save or update the BranchRequisition details
   *
   * @param payload - domain object
   * @return - the saved/updated BranchRequisition details object
   */
  public List<BranchRequisitionDto> saveOrUpdate(List<BranchRequisitionDto> payload) {
    List<BranchRequisitionDto> branchRequisitionList = new ArrayList<>();
    for (BranchRequisitionDto branchRequisitionDto : payload) {

      BranchRequisition branchRequisition = new BranchRequisition();
      if (branchRequisitionDto.getId() != null) {
        branchRequisition = findEntityById(branchRequisitionDto.getId());
      }
      modelMapper.map(branchRequisitionDto, branchRequisition);

      branchRequisition.setRecordStatus(true);
      BranchRequisition savedBranchRequisition = branchRequisitionRepository.save(branchRequisition);
      BranchRequisitionDto savedBranchRequisitionDto = modelMapper.map(savedBranchRequisition, BranchRequisitionDto.class);
      branchRequisitionList.add(savedBranchRequisitionDto);
    }
    return branchRequisitionList;
  }

  private BranchRequisition findEntityById(Long id) {
    Optional<BranchRequisition> optionalBranchRequisition = branchRequisitionRepository.findByIdAndRecordStatusTrue(id);
    if (!optionalBranchRequisition.isPresent()) {
      throw new NotFoundException(ErrorCodes.BRANCH_REQUISITION_NOT_FOUND, "branch requisition particular already exist!");
    }
    return optionalBranchRequisition.get();
  }

  /**
   * Used to check branch requisition exist or not
   *
   * @param branchRequisitionDto - {@link BranchRequisition} identifier
   * @return - return branchRequisition particular exist or not
   */
  public boolean isBranchRequisitionExist(BranchRequisitionDto branchRequisitionDto) {
    Long id = branchRequisitionDto.getId();
    Optional<BranchRequisition> optionalBranchRequisition = branchRequisitionRepository.findByIdNotAndRecordStatusTrue(id == null ? 0 : id);
    return optionalBranchRequisition.isPresent();
  }

  /**
   * Used to get the list of branch requisition particulars
   *
   * @return - list of branch requisition particulars
   */
  public List<BranchRequisitionDto> findAll() {
    List<BranchRequisition> branchRequisitionList = branchRequisitionRepository.findAllByRecordStatusTrueOrderByIdDesc();
    return branchRequisitionList.stream()
            .map(branchRequisition -> modelMapper.map(branchRequisition, BranchRequisitionDto.class))
            .collect(Collectors.toList());
  }

  public List<BranchRequisitionDto> findBranchRequisitionById(Long serviceId) {
    List<BranchRequisition> branchRequisitionList = branchRequisitionRepository.findByServiceIdAndRecordStatusTrue(serviceId);
    return branchRequisitionList.stream()
            .map(branchRequisition -> modelMapper.map(branchRequisition, BranchRequisitionDto.class))
            .collect(Collectors.toList());
  }

  public List<BillDetailsDto> saveUploadedFiles(List<BillDetailsDto> payload) {

    List<BillDetailsDto> billDetailsDtoList = new ArrayList<>();

    for (BillDetailsDto billDetailsDto : payload) {

      BillDetails billDetails = new BillDetails();
      if (billDetailsDto.getId() != null) {
        billDetails = findEntityByBillDetailsId(billDetailsDto.getId());
      }
      modelMapper.map(billDetailsDto, billDetails);

      if (billDetailsDto.getDocumentStorageDto().getDocument() != null || billDetailsDto.getDocumentStorageDto().getDocument().length != 0) {
        DocumentStorage documentStorage = documentStorageRepository.findByIdAndRecordStatusTrue(billDetailsDto.getDocumentStorageDto().getId());
        documentStorageRepository.save(documentStorage);
      }
      billDetails.setRecordStatus(true);
      BillDetails savedBillDetails = billDetailsRepository.save(billDetails);
      BillDetailsDto savedBillDetailsDto = modelMapper.map(savedBillDetails, BillDetailsDto.class);
      billDetailsDtoList.add(savedBillDetailsDto);
    }
    return billDetailsDtoList;
  }

  private BillDetails findEntityByBillDetailsId(Long id) {
    Optional<BillDetails> optionalBillDetails = billDetailsRepository.findByIdAndRecordStatusTrue(id);
    if (!optionalBillDetails.isPresent()) {
      throw new NotFoundException(ErrorCodes.BILL_DETAILS_NOT_FOUND, "bill details not foundt!");
    }
    return optionalBillDetails.get();
  }
}
