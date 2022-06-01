package com.finalproject.bootcoinnttpf.serviceimpl;

import com.finalproject.bootcoinnttpf.dto.AccountRequest;
import com.finalproject.bootcoinnttpf.dto.AccountResponse;
import com.finalproject.bootcoinnttpf.exception.account.AccountCreationException;
import com.finalproject.bootcoinnttpf.exception.account.AccountNotFoundException;
import com.finalproject.bootcoinnttpf.repository.AccountRepository;
import com.finalproject.bootcoinnttpf.service.AccountService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private static final String NOT_FOUND_MESSAGE = "Account not found with id: ";
    private static final String NOT_FOUND_MESSAGE_WITH_PHONE = "Account not found with id: {}";

    @Autowired
    private AccountRepository accountRepository;


    @Override
    public Flux<AccountResponse> getAll() {
        return accountRepository.findAll()
                .map(AccountResponse::fromModel)
                .doOnComplete(() -> log.info("Retrieving all Accounts"));
    }

    @Override
    public Mono<AccountResponse> getById(String id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(NOT_FOUND_MESSAGE + id)))
                .doOnError(ex -> log.error(NOT_FOUND_MESSAGE_WITH_PHONE, id, ex))
                .map(AccountResponse::fromModel);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(NOT_FOUND_MESSAGE + id)))
                .doOnError(ex -> log.error(NOT_FOUND_MESSAGE_WITH_PHONE, id, ex))
                .flatMap(existingAccount ->
                        accountRepository.delete(existingAccount)
                )
                .doOnSuccess(ex -> log.info("Delete account with id: {}", id));
    }

    @Override
    public Mono<Void> deleteAll() {
        return accountRepository.deleteAll()
                .doOnSuccess(ex -> log.info("Delete all accounts"));
    }

    @Override
    public Mono<AccountResponse> updateAmountById(String id, Double amount) {
        return accountRepository.findById(id)
                .map(accountFound -> {
                    accountFound.setBalance(accountFound.getBalance() + amount);
                    return accountFound;
                })
                .flatMap(account -> accountRepository.save(account))
                .doOnSuccess(x -> System.out.println("Account saved" + x))
                .map(AccountResponse::fromModel)
                .onErrorMap(ex -> new AccountCreationException(ex.getMessage()))
                .doOnSuccess(res -> log.info("Updated account with id: {}", res.getPhone()))
                .doOnError(ex -> log.error("Error updating account ", ex));
    }

    @Override
    public Mono<AccountResponse> save(AccountRequest accountRequest) {
        return Mono.just(accountRequest)
                .map(AccountRequest::toModel)
                .flatMap(account -> accountRepository.save(account))
                .map(AccountResponse::fromModel)
                .onErrorMap(ex -> new AccountCreationException(ex.getMessage()))
                .doOnSuccess(res -> log.info("Created new account with id: {}", res.getPhone()))
                .doOnError(ex -> log.error("Error creating new Account ", ex));
    }

    @Override
    public Mono<AccountResponse> updateById(String id, AccountRequest accountRequest) {
        return accountRepository.findById(id)
                .map(accountFound -> {
                    accountFound.setPayment(accountRequest.getPayment());
                    accountFound.setAccount(accountRequest.getAccount());
                    accountFound.setPhone(accountRequest.getPhone());
                    return accountFound;
                })
                .flatMap(account -> accountRepository.save(account))
                .map(AccountResponse::fromModel)
                .onErrorMap(ex -> new AccountCreationException(ex.getMessage()))
                .doOnSuccess(res -> log.info("Updated account with id: {}", res.getPhone()))
                .doOnError(ex -> log.error("Error updating account ", ex));
    }
}
