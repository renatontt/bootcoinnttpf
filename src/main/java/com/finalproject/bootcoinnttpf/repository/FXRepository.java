package com.finalproject.bootcoinnttpf.repository;

import com.finalproject.bootcoinnttpf.model.FX;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface FXRepository extends ReactiveMongoRepository<FX,String> {
    Flux<FX> findFXByCurrency(String currency);
}
