package com.finalproject.bootcoinnttpf.serviceimpl;

import com.finalproject.bootcoinnttpf.dto.TransactionAccept;
import com.finalproject.bootcoinnttpf.dto.TransactionRequest;
import com.finalproject.bootcoinnttpf.dto.TransactionResponse;
import com.finalproject.bootcoinnttpf.exception.account.AccountCreationException;
import com.finalproject.bootcoinnttpf.exception.account.AccountNotFoundException;
import com.finalproject.bootcoinnttpf.model.FX;
import com.finalproject.bootcoinnttpf.model.Transaction;
import com.finalproject.bootcoinnttpf.repository.AccountRepository;
import com.finalproject.bootcoinnttpf.repository.TransactionRepository;
import com.finalproject.bootcoinnttpf.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private static final String NOT_FOUND_MESSAGE = "Transaction not found with id: ";
    private static final String NOT_FOUND_MESSAGE_WITH_ID = "Transaction not found with id: {}";

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageServiceImpl messageService;

    private final RMapReactive<String, Transaction> transactionMap;

    private final RMapReactive<String, FX> fxMap;

    public TransactionServiceImpl(RedissonReactiveClient client) {
        this.transactionMap = client.getMap("transaction", new TypedJsonJacksonCodec(String.class, Transaction.class));
        this.fxMap = client.getMap("fx", new TypedJsonJacksonCodec(String.class, FX.class));
    }

    @Override
    public Flux<TransactionResponse> getAll() {
        return transactionRepository.findAll()
                .map(TransactionResponse::fromModel)
                .doOnComplete(() -> log.info("Retrieving all transactions"));
    }

    @Override
    public Mono<TransactionResponse> getById(String id) {
        return transactionMap.get(id)
                .switchIfEmpty(
                        transactionRepository.findById(id)
                                .switchIfEmpty(Mono.error(new AccountNotFoundException(NOT_FOUND_MESSAGE + id)))
                                .doOnError(ex -> log.error(NOT_FOUND_MESSAGE_WITH_ID, id, ex))
                                .flatMap(a -> transactionMap.fastPut(id, a).thenReturn(a))
                ).map(TransactionResponse::fromModel);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(NOT_FOUND_MESSAGE + id)))
                .doOnError(ex -> log.error(NOT_FOUND_MESSAGE_WITH_ID, id, ex))
                .flatMap(existingTransaction -> transactionMap.fastRemove(id).thenReturn(existingTransaction))
                .flatMap(existingTransaction ->
                        transactionRepository.delete(existingTransaction)
                )
                .doOnSuccess(ex -> log.info("Delete transaction with id: {}", id));
    }

    @Override
    public Mono<Void> deleteAll() {
        return transactionMap.delete()
                .then(transactionRepository.deleteAll())
                .doOnSuccess(ex -> log.info("Delete all transactions"));
    }

    @Override
    public Mono<TransactionResponse> save(TransactionRequest transactionRequest) {
        return Mono.just(transactionRequest)
                .map(TransactionRequest::toModel)
                .flatMap(transaction -> fxMap.get("bootcoin")
                        .defaultIfEmpty(new FX("bootcoin", 5.0))
                        .flatMap(fx -> {
                            transaction.setFx(fx.getFx());
                            transaction.setAmountFx(transaction.getAmount() * fx.getFx());
                            return transactionRepository.save(transaction);
                        }))
                .map(TransactionResponse::fromModel)
                .onErrorMap(ex -> new AccountCreationException(ex.getMessage()))
                .doOnSuccess(res -> log.info("Created new transaction"))
                .doOnError(ex -> log.error("Error creating new transaction ", ex));
    }

    @Override
    public Mono<TransactionResponse> accept(TransactionAccept transactionAccept) {

        return accountRepository.findById(transactionAccept.getUserId())
                .switchIfEmpty(Mono.error(new AccountNotFoundException("User does not exist")))
                .flatMap(user -> transactionRepository.findById(transactionAccept.getTransactionId())
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Not found transaction")))
                        .flatMap(transaction -> {
                            if (user.getBalance() < transaction.getAmount()) {
                                return Mono.error(new AccountCreationException("Not enough bootcoins"));
                            }

                            if (transaction.getExpiration().compareTo(LocalDateTime.now()) < 0) {
                                return Mono.error(new AccountCreationException("The transaction has expired"));
                            }

                            user.setBalance(user.getBalance() - transaction.getAmount());
                            return accountRepository.save(user).thenReturn(transaction);

                        })).flatMap(transaction -> {
                    transaction.setTo(transactionAccept.getUserId());
                    transaction.setState("Pending of payment");
                    transaction.setTransactionId(transaction.getId());
                    messageService.sendAcceptMessage(transaction);
                    return transactionMap.fastPut(transaction.getTransactionId(), transaction)
                            .thenReturn(transaction);
                })
                .flatMap(transaction -> transactionRepository.save(transaction))
                .map(TransactionResponse::fromModel)
                .onErrorMap(ex -> new AccountCreationException(ex.getMessage()))
                .doOnSuccess(res -> log.info("Accepted transaction"))
                .doOnError(ex -> log.error("Error accepting transaction ", ex));
    }

    private Mono<Transaction> saveOnRedis(Transaction transaction) {
        return transactionMap.fastPut(transaction.getId(), transaction)
                .thenReturn(transaction);
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void cancelExpiredTransactions() {
        transactionRepository.findAll()
                .filter(transaction -> !transaction.getState().equals("Completed"))
                .filter(transaction -> transaction.getExpiration().compareTo(LocalDateTime.now())<0)
                .flatMap(transaction -> {
                    if (transaction.getState().equals("Pending of payment")) {
                        transaction.setState("Expìred");
                        return accountRepository.findById(transaction.getFrom())
                                .map(account -> {
                                    account.setBalance(account.getBalance() + transaction.getAmount());
                                    return account;
                                }).then(saveOnRedis(transaction));
                    }
                    transaction.setState("Expìred");
                    return saveOnRedis(transaction);
                })
                .flatMap(transaction -> transactionRepository.save(transaction))
                .subscribe();
    }

}
