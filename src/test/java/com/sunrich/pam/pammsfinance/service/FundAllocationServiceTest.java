package com.sunrich.pam.pammsfinance.service;

import com.sunrich.pam.common.domain.finance.BankPayment;
import com.sunrich.pam.common.domain.finance.FundAllocation;
import com.sunrich.pam.common.domain.pda.PdaData;
import com.sunrich.pam.common.dto.finance.BankPaymentDto;
import com.sunrich.pam.common.dto.finance.FundAllocationDto;
import com.sunrich.pam.common.dto.finance.JobDetailsProjection;
import com.sunrich.pam.common.dto.pda.PdaDto;
import com.sunrich.pam.common.exception.NotFoundException;
import com.sunrich.pam.pammsfinance.repository.BankPaymentRepository;
import com.sunrich.pam.pammsfinance.repository.FundAllocationRepository;
import com.sunrich.pam.pammsfinance.repository.PdaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class FundAllocationServiceTest {

  @InjectMocks
  private FundAllocationService fundAllocationService;

  @Mock
  private FundAllocationRepository fundAllocationRepository;

  @Mock
  private PdaRepository pdaRepository;

  @Mock
  private BankPaymentRepository bankPaymentRepository;

  @Spy
  private ModelMapper modelMapper;

  private List<FundAllocation> fundAllocationList;
  private List<FundAllocationDto> fundAllocationDtoList;
  private List<PdaData> pdaDataList;
  private List<PdaDto> pdaDtoList;

  @Before
  public void setUp() {
    BankPaymentDto bankPaymentDto = BankPaymentDto.builder().id(1L).build();
    fundAllocationList = Arrays.asList(
            FundAllocation.builder().id(1L).jobNo("LO-VAD03190001").bankPaymentId(1L).recordStatus(true).build(),
            FundAllocation.builder().id(2L).jobNo("LO-VAD03190002").bankPaymentId(1L).recordStatus(true).build()
    );
    fundAllocationDtoList = Arrays.asList(
            FundAllocationDto.builder().id(1L).jobNo("LO-VAD03190001").bankPaymentId(1L).bankPaymentDto(bankPaymentDto).build(),
            FundAllocationDto.builder().id(2L).jobNo("LO-VAD03190002").bankPaymentId(1L).bankPaymentDto(bankPaymentDto).build()
    );
    pdaDataList = Arrays.asList(
            PdaData.builder().id(1L).jobNo("LO-VAD03190001").recordStatus(true).build(),
            PdaData.builder().id(2L).jobNo("LO-VAD03190002").recordStatus(true).build()
    );
    pdaDtoList = Arrays.asList(
            PdaDto.builder().id(1L).jobNo("LO-VAD03190001").build(),
            PdaDto.builder().id(2L).jobNo("LO-VAD03190002").build()
    );
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
  }

  @Test
  public void save_whenVaidObject_shouldSaveAndReturnSavedObject() {
    FundAllocation.FundAllocationBuilder builder = FundAllocation.builder().id(null).jobNo("LO-VAD03190001");

    FundAllocation paymentToSave = builder.recordStatus(true).build();
    FundAllocation savedPayment = builder.id(1L).recordStatus(true).build();

    FundAllocationDto newDto = FundAllocationDto.builder().id(null).jobNo("LO-VAD03190001").build();
    FundAllocationDto savedDto = FundAllocationDto.builder().id(1L).jobNo("LO-VAD03190001").build();

    when(fundAllocationRepository.save(paymentToSave)).thenReturn(savedPayment);
    assertThat(fundAllocationService.saveOrUpdate(fundAllocationDtoList)).isEqualTo(fundAllocationDtoList);
    verify(fundAllocationRepository).save(paymentToSave);
  }

  @Test
  public void update_whenValidObject_shouldSaveAndReturnSavedObject() {
    String jobNumber = "LO-VAD03190003";

    FundAllocation.FundAllocationBuilder builder = FundAllocation.builder().id(1L).jobNo("LO-VAD03190001");

    FundAllocation findByIdResult = builder.jobNo("LO-VAD03190001").recordStatus(true).build();
    FundAllocation updatedFundAllocation = builder.jobNo(jobNumber).recordStatus(true).build();

    FundAllocationDto payload = FundAllocationDto.builder().id(1L).jobNo(jobNumber).build();

    when(fundAllocationRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.ofNullable(findByIdResult));
    when(fundAllocationRepository.save(updatedFundAllocation)).thenReturn(updatedFundAllocation);
    assertThat(fundAllocationService.saveOrUpdate(fundAllocationDtoList)).isEqualTo(payload);
    verify(fundAllocationRepository).findByIdAndRecordStatusTrue(1L);
    verify(fundAllocationRepository).save(updatedFundAllocation);
  }

  @Test(expected = NotFoundException.class)
  public void update_whenNonExistingObject_shouldThrowNotFoundException() {
    FundAllocationDto fundAllocationDto = FundAllocationDto.builder().id(1L).build();
    when(fundAllocationRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.empty());
    try {
      fundAllocationService.saveOrUpdate(fundAllocationDtoList);
    } finally {
      verify(fundAllocationRepository).findByIdAndRecordStatusTrue(1L);
    }
  }

  @Test
  public void findById_whenRecordExist_shouldReturnRecord() {
    BankPayment bankPayment = BankPayment.builder().id(1L).recordStatus(true).build();
    when(fundAllocationRepository.findByJobNoAndRecordStatusTrue("LO-VAD03190001")).thenReturn(fundAllocationList);
    when(bankPaymentRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.ofNullable(bankPayment));

    assertThat(fundAllocationService.findAllocatedFundByJobNumber("LO-VAD03190001")).isEqualTo(fundAllocationDtoList);
    verify(fundAllocationRepository).findByJobNoAndRecordStatusTrue("LO-VAD03190001");

  }

  @Test(expected = NotFoundException.class)
  public void findById_whenRecordDontExist_thrwoNotFoundException() {
    fundAllocationService.findAllocatedFundByJobNumber("LO-VAD03190025");
    verify(fundAllocationRepository).findByJobNoAndRecordStatusTrue("LO-VAD03190025");
  }

  @Test
  public void findAllJobByCustomerId_whenRecordExist_shouldReturnRecord() {
    when(pdaRepository.findByCustomerAndPdaStatusAndRecordStatusTrue(1L,"ACC")).thenReturn(pdaDataList);
    assertThat(fundAllocationService.findAllJobByCustomerId(1L)).isEqualTo(pdaDtoList);
    verify(pdaRepository).findByCustomerAndPdaStatusAndRecordStatusTrue(1L,"ACC");
  }
}
