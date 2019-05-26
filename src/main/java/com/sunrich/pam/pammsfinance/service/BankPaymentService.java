package com.sunrich.pam.pammsfinance.service;

import com.sunrich.pam.common.domain.Customer;
import com.sunrich.pam.common.domain.finance.BankPayment;
import com.sunrich.pam.common.dto.CustomerDTO;
import com.sunrich.pam.common.dto.finance.BankPaymentDto;
import com.sunrich.pam.common.dto.finance.BankPaymentProjection;
import com.sunrich.pam.common.exception.ErrorCodes;
import com.sunrich.pam.common.exception.NotFoundException;
import com.sunrich.pam.pammsfinance.repository.BankPaymentRepository;
import com.sunrich.pam.pammsfinance.repository.CustomerRepository;
import com.sunrich.pam.pammsfinance.repository.FundAllocationRepository;
import com.sunrich.pam.pammsfinance.repository.PdaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankPaymentService {
  private BankPaymentRepository bankPaymentRepository;
  private PdaRepository pdaRepository;
  private CustomerRepository customerRepository;
  private ModelMapper modelMapper;
  private FundAllocationRepository fundAllocationRepository;

  public BankPaymentService(BankPaymentRepository bankPaymentRepository, PdaRepository pdaRepository, CustomerRepository customerRepository, ModelMapper modelMapper, FundAllocationRepository fundAllocationRepository) {
    this.bankPaymentRepository = bankPaymentRepository;
    this.pdaRepository = pdaRepository;
    this.customerRepository = customerRepository;
    this.modelMapper = modelMapper;
    this.fundAllocationRepository = fundAllocationRepository;
  }

  /**
   * used to find list of bank payments
   *
   * @return list of bank payments
   */
  public List<BankPaymentDto> findAll() {
    List<BankPayment> bankPayments = bankPaymentRepository.findAllByRecordStatusTrue();
    List<BankPaymentDto> bankPaymentDtoList = getBankPaymentDtoList(bankPayments);
    return bankPaymentDtoList;
  }

  /**
   * used to find a bank payment using id
   *
   * @param id -bank payment identifier
   * @return bank payment object
   */
  public BankPaymentDto findById(Long id) {
    BankPayment bankPayment = findEntityById(id);
    BankPaymentDto bankPaymentDto = modelMapper.map(bankPayment, BankPaymentDto.class);
    BankPaymentProjection projection = bankPaymentRepository.findBankName(bankPaymentDto.getId());
    bankPaymentDto.setBankName(projection.getBankName());
    return bankPaymentDto;
  }

  private BankPayment findEntityById(Long id) {
    Optional<BankPayment> bankPayment = bankPaymentRepository.findByIdAndRecordStatusTrue(id);
    if (!bankPayment.isPresent()) {
      throw new NotFoundException(ErrorCodes.PAYMENT_NOT_FOUND, "Bank payment not found");
    }
    return bankPayment.get();
  }

  public List<BankPaymentDto> saveOrUpdateBulk(List<BankPaymentDto> bankPaymentDtoList) {
    List<BankPaymentDto> savedBankPaymentDtos = new ArrayList<>();
    for (BankPaymentDto bankPaymentDto : bankPaymentDtoList) {
      BankPaymentDto dto = saveOrUpdate(bankPaymentDto);
      savedBankPaymentDtos.add(dto);
    }
    /*List<BankPayment> bankPaymentList = bankPaymentDtoList.stream().map(bankPaymentDto -> modelMapper.map(bankPaymentDto, BankPayment.class)).collect(Collectors.toList());
    for (BankPayment bankPayment : bankPaymentList) {
      bankPayment.setRecordStatus(true);
      bankPayment.setRoe(bankPayment.getGrossAmountReceivedLocal().divide(bankPayment.getAmountReceived(), 6, RoundingMode.HALF_UP));
    }
    List<BankPayment> savedBankPaymentList = bankPaymentRepository.saveAll(bankPaymentList);
    return savedBankPaymentList.stream()
            .map(bank -> modelMapper.map(bank, BankPaymentDto.class))
            .collect(Collectors.toList());*/
    return savedBankPaymentDtos;
  }


  /**
   * used to save or update bank payment
   *
   * @param bankPaymentDto -domain object
   * @return save/updated bank payment object
   */
  public BankPaymentDto saveOrUpdate(BankPaymentDto bankPaymentDto) {
    BankPayment bankPayment = new BankPayment();
    if (bankPaymentDto.getId() != null) {
      bankPayment = findEntityById(bankPaymentDto.getId());
    }
    modelMapper.map(bankPaymentDto, bankPayment);
    bankPayment.setRecordStatus(true);
    bankPayment.setRoe(bankPayment.getGrossAmountReceivedLocal().divide(bankPayment.getAmountReceived(), 6, RoundingMode.HALF_UP));
    if (bankPaymentDto.getIsApproved() == null || bankPaymentDto.getIsApproved().equals(false)) {
      bankPayment.setIsApproved(null);
    }
    BankPayment savedBankPayment = bankPaymentRepository.save(bankPayment);
    return modelMapper.map(savedBankPayment, BankPaymentDto.class);
  }

  /**
   * delete a particular bank payment
   *
   * @param id -bank payment identifier
   * @return deleted bank payment id
   */
  public Long delete(Long id) {
    BankPayment bankPayment = findEntityById(id);
    bankPayment.setRecordStatus(false);
    bankPaymentRepository.save(bankPayment);
    return bankPayment.getId();
  }

  /**
   * used to find list of all customers with there payment history
   *
   * @return list of customers
   */
  public List<CustomerDTO> findAllCustomer() {
    List<Long> customerIds = pdaRepository.findCustomerIdByPdaStatus("ACC");
    List<Customer> customerList = customerRepository.findByIdInAndRecordStatusTrue(customerIds);
    List<CustomerDTO> customerDTOList = customerList.stream()
            .map(customer -> modelMapper.map(customer, CustomerDTO.class))
            .collect(Collectors.toList());
    for (CustomerDTO customer : customerDTOList) {
      customer.setPaymentList(findByCustomerId(customer.getId()));
    }
    return customerDTOList;
  }

  /**
   * used to get list of all bank payment using customer id
   *
   * @param id
   * @return list of bank payment
   */
  public List<BankPaymentDto> findByCustomerId(Long id) {
    List<BankPayment> bankPaymentList = bankPaymentRepository.findByCustomerIdAndRecordStatusTrue(id);
    List<BankPaymentDto> bankPaymentDtoList = getBankPaymentDtoList(bankPaymentList);
    return bankPaymentDtoList;
  }

  private List<BankPaymentDto> getBankPaymentDtoList(List<BankPayment> bankPaymentList) {
    List<BankPaymentDto> bankPaymentDtoList = new ArrayList<>();
    for (BankPayment bankPayment : bankPaymentList) {
      BankPaymentDto bankPaymentDto = modelMapper.map(bankPayment, BankPaymentDto.class);
      BankPaymentProjection projection = bankPaymentRepository.findBankName(bankPaymentDto.getId());
      bankPaymentDto.setBankName(projection.getBankName());
      bankPaymentDtoList.add(bankPaymentDto);
    }

    return bankPaymentDtoList;
  }

  public List<BankPaymentDto> findSuspenseBankPayment() {
    List<BankPayment> bankPaymentList = bankPaymentRepository.findByCustomerIdNullAndRecordStatusTrue();
    List<BankPaymentDto> bankPaymentDtoList = getBankPaymentDtoList(bankPaymentList);
    return bankPaymentDtoList;
  }

  public List<BankPaymentDto> getBankReferenceNoSuggestion(Long customerId) {
    List<BankPayment> bankPaymentList = bankPaymentRepository.findByCustomerIdAndRecordStatusTrueAndIsApprovedTrue(customerId);

    Map<Long, BigDecimal> map = getBankPaymentIdAndAllocatedAmountSum();

    List<BankPaymentDto> bankPaymentDtoList = new ArrayList<>();

    for (BankPayment bankPayment : bankPaymentList) {
      if (map.containsKey(bankPayment.getId())) {
        BigDecimal allocatedFund = map.get(bankPayment.getId());
        if (!bankPayment.getNetAmountReceivedLocal().subtract(allocatedFund).equals(BigDecimal.ZERO) && bankPayment.getNetAmountReceivedLocal().subtract(allocatedFund).compareTo(BigDecimal.ZERO) > 0) {
          bankPaymentDtoList.add(modelMapper.map(bankPayment, BankPaymentDto.class));
        }
      } else {
        bankPaymentDtoList.add(modelMapper.map(bankPayment, BankPaymentDto.class));
      }
    }

    return bankPaymentDtoList;
  }


  public BankPaymentDto approveOrReject(Long id, String bankReferenceNo, Boolean isApproved) {
    Optional<BankPayment> optional = bankPaymentRepository.findByIdAndBankReferenceNoAndRecordStatusTrue(id, bankReferenceNo);
    if (!optional.isPresent()) {
      throw new NotFoundException(ErrorCodes.PAYMENT_NOT_FOUND, "Bank payment not found");
    }
    BankPayment bankPayment = optional.get();
    bankPayment.setIsApproved(isApproved);
    BankPayment savedBankPayment = bankPaymentRepository.save(bankPayment);
    return modelMapper.map(savedBankPayment, BankPaymentDto.class);
  }

  public List<BankPaymentDto> getCustomerPaymentList() {

    Map<Long, BigDecimal> map = getBankPaymentIdAndAllocatedAmountSum();

    Map<Long, BankPaymentDto> bankPaymentMap = new HashMap<>();
    List<BankPayment> bankPaymentList = bankPaymentRepository.findAllCustomerPaymentList();
    for (BankPayment bankPayment : bankPaymentList) {
      if (map.containsKey(bankPayment.getId())) {
        BigDecimal allocatedAmount = map.get(bankPayment.getId());
        bankPayment.setNetAmountReceivedLocal(bankPayment.getNetAmountReceivedLocal().subtract(allocatedAmount));
        bankPayment.setAmountReceived(bankPayment.getAmountReceived().subtract(allocatedAmount.divide(bankPayment.getRoe(), 2, RoundingMode.HALF_UP)));
      }
      if (bankPaymentMap.containsKey(bankPayment.getCustomerId())) {
        BankPaymentDto payment = bankPaymentMap.get(bankPayment.getCustomerId());
        payment.setNetAmountReceivedLocal(bankPayment.getNetAmountReceivedLocal().add(payment.getNetAmountReceivedLocal()));
        payment.setAmountReceived(bankPayment.getAmountReceived().add(payment.getAmountReceived()));
        payment.setRoe(null);
        payment.setCurrency(null);
        payment.setRemitter(null);
        payment.setCustomerName(customerRepository.findCustomerName(bankPayment.getCustomerId()));
        bankPaymentMap.put(bankPayment.getCustomerId(), payment);
      } else {
        bankPaymentMap.put(bankPayment.getCustomerId(), modelMapper.map(bankPayment, BankPaymentDto.class));
      }
    }

    return new ArrayList<>(bankPaymentMap.values());
  }

  private Map<Long, BigDecimal> getBankPaymentIdAndAllocatedAmountSum() {
    List<Tuple> objects = fundAllocationRepository.getBankPaymentIdAndAllocatedAmount();
    Map<Long, BigDecimal> map = new HashMap<>();
    objects.forEach(record -> {
      map.put((Long) record.get("bankPaymentId"), (BigDecimal) record.get("allocatedFund"));
    });
    return map;
  }


}
