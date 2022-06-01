package com.finalproject.bootcoinnttpf.service;

import com.finalproject.bootcoinnttpf.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Flux<TransactionResponse> getAll();

    Mono<TransactionResponse> getById(String id);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteAll();
    Mono<TransactionResponse> save(TransactionRequest transactionRequest);

    Mono<TransactionResponse> accept(TransactionAccept transactionAccept);
}
