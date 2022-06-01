package com.finalproject.bootcoinnttpf.repository;

import com.finalproject.bootcoinnttpf.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AccountRepository extends ReactiveMongoRepository<Account,String> {

}
