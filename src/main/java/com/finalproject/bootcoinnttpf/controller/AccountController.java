package com.finalproject.bootcoinnttpf.controller;

import com.finalproject.bootcoinnttpf.dto.AccountRequest;
import com.finalproject.bootcoinnttpf.dto.AccountResponse;
import com.finalproject.bootcoinnttpf.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bootcoin/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {
    private AccountService service;

    @GetMapping
    public Flux<AccountResponse> getAllAccounts() {
        return service.getAll();
    }

    @GetMapping("{id}")
    public Mono<AccountResponse> getAccount(@PathVariable final String id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountResponse> saveAccount(@RequestBody final AccountRequest accountRequest) {
        return service.save(accountRequest);
    }

    @PutMapping("{id}")
    public Mono<AccountResponse> updateAccount(@PathVariable final String id,
                                               @RequestBody final AccountRequest accountRequest) {
        return service.updateById(id, accountRequest);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteAccount(@PathVariable final String id) {
        return service.deleteById(id);
    }

    @DeleteMapping
    public Mono<Void> deleteAllAccounts() {
        return service.deleteAll();
    }
}
