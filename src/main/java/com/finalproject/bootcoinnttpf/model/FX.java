package com.finalproject.bootcoinnttpf.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
@Document(collection = "fx")
public class FX {
    private String currency;
    private double fx;
}
