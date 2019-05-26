package com.sunrich.pam.pammsfinance.service;

import com.sunrich.pam.common.domain.finance.BranchRequisition;
import com.sunrich.pam.common.dto.finance.BranchRequisitionDto;
import com.sunrich.pam.pammsfinance.repository.BranchRequisitionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
public class BranchRequisitionServiceTest {

  @InjectMocks
  BranchRequisitionService branchRequisitionService;

  @Mock
  BranchRequisitionRepository branchRequisitionRepository;

  @Spy
  ModelMapper modelMapper;

  private List<BranchRequisition> branchRequisitionList;
  private List<BranchRequisitionDto> branchRequisitionDtoList;

  @Before
  public void setUp() {
    branchRequisitionList = Arrays.asList(
            BranchRequisition.builder().id(1L).serviceId(2L).jobNo("LO-VAD03190001").amountRequested(BigDecimal.valueOf(10.5)).amountApproved(BigDecimal.valueOf(5.8)).isApproved(true).isBranchApproved(true).recordStatus(true).build(),
            BranchRequisition.builder().id(2L).serviceId(2L).jobNo("LO-VAD03190002").amountRequested(BigDecimal.valueOf(2.5)).amountApproved(BigDecimal.valueOf(2.8)).isApproved(true).isBranchApproved(true).recordStatus(true).build()
    );
    branchRequisitionDtoList = Arrays.asList(
            BranchRequisitionDto.builder().id(1L).serviceId(2L).jobNo("LO-VAD03190001").amountRequested(BigDecimal.valueOf(10.5)).amountApproved(BigDecimal.valueOf(5.8)).isApproved(true).isBranchApproved(true).build(),
            BranchRequisitionDto.builder().id(2L).serviceId(2L).jobNo("LO-VAD03190002").amountRequested(BigDecimal.valueOf(2.5)).amountApproved(BigDecimal.valueOf(2.8)).isApproved(true).isBranchApproved(true).build()
    );
  }

  @Test
  public void findAllById_whenRecordExist_shouldReturnRecord() {

    when(branchRequisitionRepository.findAllByRecordStatusTrueOrderByIdDesc()).thenReturn(branchRequisitionList);
    assertThat(branchRequisitionService.findAll()).isEqualTo(branchRequisitionDtoList);
    verify(branchRequisitionRepository).findAllByRecordStatusTrueOrderByIdDesc();
  }

  @Test
  public void getBranchRequisitionParticularDetailsByServiceId_whenRecordExist_shouldReturnRecord() {

    when(branchRequisitionRepository.findByServiceIdAndRecordStatusTrue(2L)).thenReturn(branchRequisitionList);
    assertThat(branchRequisitionService.findBranchRequisitionById(2L)).isEqualTo(branchRequisitionDtoList);
    verify(branchRequisitionRepository).findByServiceIdAndRecordStatusTrue(2L);
  }
}
