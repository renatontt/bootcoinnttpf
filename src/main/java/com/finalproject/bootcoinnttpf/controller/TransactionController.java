package com.finalproject.bootcoinnttpf.controller;

import com.finalproject.bootcoinnttpf.dto.TransactionAccept;
import com.finalproject.bootcoinnttpf.dto.TransactionRequest;
import com.finalproject.bootcoinnttpf.dto.TransactionResponse;
import com.finalproject.bootcoinnttpf.dto.TransactionResponse;
import com.finalproject.bootcoinnttpf.service.TransactionService;
import com.finalproject.bootcoinnttpf.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bootcoin/transactions")
@AllArgsConstructor
@Slf4j
public class TransactionController {
    private TransactionService service;

    @GetMapping
    public Flux<TransactionResponse> getAllTransactions() {
        return service.getAll();
    }

    @GetMapping("{id}")
    public Mono<TransactionResponse> getTransaction(@PathVariable final String id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionResponse> saveTransaction(@RequestBody final TransactionRequest accountRequest) {
        return service.save(accountRequest);
    }

    @PostMapping("/accept")
    public Mono<TransactionResponse> acceptTransaction(@RequestBody final TransactionAccept transactionAccept) {
        return service.accept(transactionAccept);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteTransaction(@PathVariable final String id) {
        return service.deleteById(id);
    }

    @DeleteMapping
    public Mono<Void> deleteAllTransactions() {
        return service.deleteAll();
    }
}
