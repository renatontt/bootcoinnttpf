package com.finalproject.bootcoinnttpf.dto;

import com.finalproject.bootcoinnttpf.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TransactionRequest {
    private String from;
    private String type;
    private String number;
    private Double amount;

    public Transaction toModel() {
        return Transaction.builder()
                .from(this.from)
                .type(this.type)
                .number(this.number)
                .amount(this.amount)
                .state("Created")
                .date(LocalDateTime.now())
                .expiration(LocalDateTime.now().plusMinutes(3))
                .build();
    }
}
