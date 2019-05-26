package com.sunrich.pam.pammsfinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.sunrich")
@EntityScan({"com.sunrich.pam.common.domain"})
public class PamMsFinanceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PamMsFinanceApplication.class, args);
  }

}
