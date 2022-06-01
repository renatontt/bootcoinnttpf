package com.finalproject.bootcoinnttpf.repository;

import com.finalproject.bootcoinnttpf.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction,String> {
}
