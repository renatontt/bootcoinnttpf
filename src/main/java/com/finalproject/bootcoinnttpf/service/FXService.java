package com.finalproject.bootcoinnttpf.service;

import com.finalproject.bootcoinnttpf.model.FX;
import reactor.core.publisher.Mono;

public interface FXService {

    Mono<Double> getFx(String currency);

    Mono<Double> setFx(FX fx);
}
