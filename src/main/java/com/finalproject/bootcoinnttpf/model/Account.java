package com.finalproject.bootcoinnttpf.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    private String documentType;
    private String documentNumber;
    private String payment;
    private String account;
    private Long phone;
    private String email;
    private Double balance;
}
