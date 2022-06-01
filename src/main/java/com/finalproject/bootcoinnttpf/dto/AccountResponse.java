package com.finalproject.bootcoinnttpf.dto;

import com.finalproject.bootcoinnttpf.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse implements Serializable {
    private String id;
    private String documentType;
    private String documentNumber;
    private String payment;
    private String account;
    private Long phone;
    private String email;
    private Double balance;

    private static final long serialVersionUID = 1L;

    public static AccountResponse fromModel(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .documentType(account.getDocumentType())
                .documentNumber(account.getDocumentNumber())
                .payment(account.getPayment())
                .account(account.getAccount())
                .phone(account.getPhone())
                .email(account.getEmail())
                .balance(account.getBalance())
                .build();
    }
}
