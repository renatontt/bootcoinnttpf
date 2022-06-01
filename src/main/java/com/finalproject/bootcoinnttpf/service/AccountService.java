package com.finalproject.bootcoinnttpf.service;

import com.finalproject.bootcoinnttpf.dto.AccountRequest;
import com.finalproject.bootcoinnttpf.dto.AccountResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Flux<AccountResponse> getAll();

    Mono<AccountResponse> getById(String id);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteAll();

    Mono<AccountResponse> updateAmountById(String id, Double amount);

    Mono<AccountResponse> save(AccountRequest accountRequest);

    Mono<AccountResponse> updateById(String id, AccountRequest accountRequest);
}
