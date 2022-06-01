package com.finalproject.bootcoinnttpf.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.finalproject.bootcoinnttpf.model.Account;
import com.finalproject.bootcoinnttpf.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String id;
    private String from;
    private String type;
    private String number;
    private String state;
    private Double amount;
    private Double fx;
    private Double amountFx;
    private String transactionId;

    private LocalDateTime date;

    private LocalDateTime expiration;

    public static TransactionResponse fromModel(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .from(transaction.getFrom())
                .type(transaction.getType())
                .number(transaction.getNumber())
                .state(transaction.getState())
                .amount(transaction.getAmount())
                .fx(transaction.getFx())
                .amountFx(transaction.getAmountFx())
                .transactionId(transaction.getTransactionId())
                .date(transaction.getDate())
                .expiration(transaction.getExpiration())
                .build();
    }
}
