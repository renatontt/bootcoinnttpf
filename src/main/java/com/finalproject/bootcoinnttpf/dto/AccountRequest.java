package com.finalproject.bootcoinnttpf.dto;

import com.finalproject.bootcoinnttpf.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AccountRequest {
    private String documentType;
    private String documentNumber;
    private String payment;
    private String account;
    private Long phone;
    private String email;

    public Account toModel() {
        return Account.builder()
                .documentType(this.documentType)
                .documentNumber(this.documentNumber)
                .payment(this.payment)
                .account(this.account)
                .phone(this.phone)
                .email(this.email)
                .balance(100.0)
                .build();
    }
}
