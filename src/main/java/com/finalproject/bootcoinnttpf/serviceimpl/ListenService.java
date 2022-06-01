package com.finalproject.bootcoinnttpf.serviceimpl;

import com.finalproject.bootcoinnttpf.dto.Result;
import com.finalproject.bootcoinnttpf.dto.TransactionEvent;
import com.finalproject.bootcoinnttpf.model.Transaction;
import com.finalproject.bootcoinnttpf.repository.AccountRepository;
import com.finalproject.bootcoinnttpf.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Service
@Slf4j
public class ListenService {

    private RMapReactive<String, Transaction> transactionMap;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageServiceImpl messageService;

    public ListenService(RedissonReactiveClient client) {
        this.transactionMap = client.getMap("transaction", new TypedJsonJacksonCodec(String.class, Transaction.class));
    }

    @Bean
    Consumer<TransactionEvent> transaction() {
        return transactionEvent -> {
            if (transactionEvent.getState().equals("Paid")) {
                transactionMap.get(transactionEvent.getTransactionId())
                        .flatMap(transaction -> {
                            transaction.setState("Completed");
                            return transactionMap.fastPut(transaction.getTransactionId(), transaction).thenReturn(transaction)
                                    .flatMap(transactionAux -> transactionRepository.save(transactionAux))
                                    .then(accountRepository.findById(transaction.getFrom()))
                                    .flatMap(account -> {
                                        account.setBalance(account.getBalance() + transaction.getAmount());
                                        return accountRepository.save(account);
                                    })
                                    .then(accountRepository.findById(transaction.getTo()))
                                    .map(account -> {
                                        messageService.sendToTransaction(transactionEvent, account);
                                        return account;
                                    })
                                    .then(Mono.just(messageService.sendResult(Result.builder()
                                            .to(transaction.getFrom())
                                            .status("Success")
                                            .message("You received " + transaction.getAmount() + " BootCoins")
                                            .build())));
                        })
                        .subscribe();
            } else if (transactionEvent.getState().equals("Completed")) {
                messageService.sendResult(Result.builder()
                        .to(transactionEvent.getUserId())
                        .status("Success")
                        .message("You received PEN" + transactionEvent.getAmount() + " to your " + transactionEvent.getTypeAccount() +
                                " account with number: " + transactionEvent.getNumber())
                        .build());
            }
        };
    }

}
