package com.sunrich.pam.pammsfinance.service;

import com.sunrich.pam.common.constants.Constants.Type;
import com.sunrich.pam.common.domain.finance.BankPayment;

import com.sunrich.pam.common.dto.finance.BankPaymentDto;

import com.sunrich.pam.common.exception.NotFoundException;
import com.sunrich.pam.pammsfinance.repository.BankPaymentRepository;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class BankPaymentServiceTest {

  @InjectMocks
  BankPaymentService bankPaymentService;

  @Mock
  BankPaymentRepository bankPaymentRepository;

  @Spy
  ModelMapper modelMapper;

  private List<BankPayment> bankPaymentList;
  private List<BankPaymentDto> bankPaymentDtoList;

  @Before
  public void setUp() {
    bankPaymentList = Arrays.asList(
            BankPayment.builder().id(1L).currency("USD").type(Type.CREDIT).recordStatus(true).build(),
            BankPayment.builder().id(2L).currency("INR").type(Type.DEBIT).recordStatus(true).build()
    );
    bankPaymentDtoList = Arrays.asList(
            BankPaymentDto.builder().id(1L).currency("USD").type(Type.CREDIT).build(),
            BankPaymentDto.builder().id(2L).currency("INR").type(Type.DEBIT).build()
    );

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
  }

  @Test
  public void findAll_whenRecordsExist_shouldReturnRecords() {
    when(bankPaymentRepository.findAllByRecordStatusTrue()).thenReturn(bankPaymentList);
    assertThat(bankPaymentService.findAll()).isEqualTo(bankPaymentDtoList);
    verify(bankPaymentRepository).findAllByRecordStatusTrue();
  }

  @Test
  public void findById_whenRecordExist_shouldReturnRecord() {
    when(bankPaymentRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.ofNullable(bankPaymentList.get(0)));
    assertThat(bankPaymentService.findById(1L)).isEqualTo(bankPaymentDtoList.get(0));
    verify(bankPaymentRepository).findByIdAndRecordStatusTrue(1L);
  }

  @Test(expected = NotFoundException.class)
  public void findById_whenRecordDontExist_thrwoNotFoundException() {
    bankPaymentService.findById(3L);
    verify(bankPaymentRepository).findByIdAndRecordStatusTrue(3L);
  }

  @Test
  public void save_whenVaidObject_shouldSaveAndReturnSavedObject() {
    BankPayment.BankPaymentBuilder builder = BankPayment.builder().id(null).type(Type.CREDIT).currency("USD");

    BankPayment paymentToSave = builder.recordStatus(true).build();
    BankPayment savedPayment = builder.id(1L).recordStatus(true).build();

    BankPaymentDto newDto = BankPaymentDto.builder().id(null).type(Type.CREDIT).currency("USD").build();
    BankPaymentDto savedDto = BankPaymentDto.builder().id(1L).type(Type.CREDIT).currency("USD").build();

    when(bankPaymentRepository.save(paymentToSave)).thenReturn(savedPayment);
    assertThat(bankPaymentService.saveOrUpdate(newDto)).isEqualTo(savedDto);
    verify(bankPaymentRepository).save(paymentToSave);
  }

  @Test
  public void update_whenValidObject_shouldSaveAndReturnSavedObject() {
    String updatedCurrency = "INR";

    BankPayment.BankPaymentBuilder builder = BankPayment.builder().id(1L).type(Type.CREDIT);

    BankPayment findByIdResult = builder.currency("USD").recordStatus(true).build();
    BankPayment updatedPayment = builder.currency(updatedCurrency).recordStatus(true).build();

    BankPaymentDto payload = BankPaymentDto.builder().id(1L).currency(updatedCurrency).type(Type.CREDIT).build();

    when(bankPaymentRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.ofNullable(findByIdResult));
    when(bankPaymentRepository.save(updatedPayment)).thenReturn(updatedPayment);
    assertThat(bankPaymentService.saveOrUpdate(payload)).isEqualTo(payload);
    verify(bankPaymentRepository).findByIdAndRecordStatusTrue(1L);
    verify(bankPaymentRepository).save(updatedPayment);
  }

  @Test(expected = NotFoundException.class)
  public void update_whenNonExistingObject_shouldThrowNotFoundException() {
    BankPaymentDto bankPaymentDto = BankPaymentDto.builder().id(1L).build();
    when(bankPaymentRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.empty());
    try {
      bankPaymentService.saveOrUpdate(bankPaymentDto);
    } finally {
      verify(bankPaymentRepository).findByIdAndRecordStatusTrue(1L);
    }
  }

  @Test
  public void delete_whenRecordWithGivenIdExists_shouldUpdateRecordStatusToFalseAndReturnTheId() {
    BankPayment.BankPaymentBuilder builder = BankPayment.builder().id(1L).type(Type.CREDIT).currency("USD");
    BankPayment bankPayment = builder.recordStatus(true).build();

    when(bankPaymentRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.ofNullable(bankPayment));

    BankPayment bankPaymentToDelete = builder.recordStatus(false).build();
    when(bankPaymentRepository.save(bankPaymentToDelete)).thenReturn(bankPaymentToDelete);

    assertThat(bankPaymentService.delete(1L)).isEqualTo(1L);

    verify(bankPaymentRepository).findByIdAndRecordStatusTrue(1L);
    verify(bankPaymentRepository).save(bankPaymentToDelete);
  }

  @Test(expected = NotFoundException.class)
  public void delete_whenRecordWithGivenIdIsNotPresent_shouldThrowNotFoundException() {
    when(bankPaymentRepository.findByIdAndRecordStatusTrue(1L)).thenReturn(Optional.empty());
    try {
      bankPaymentService.delete(1L);
    } finally {
      verify(bankPaymentRepository).findByIdAndRecordStatusTrue(1L);
    }
  }
}
