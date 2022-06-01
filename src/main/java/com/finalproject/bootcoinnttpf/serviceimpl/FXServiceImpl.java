package com.finalproject.bootcoinnttpf.serviceimpl;

import com.finalproject.bootcoinnttpf.exception.account.AccountNotFoundException;
import com.finalproject.bootcoinnttpf.model.FX;
import com.finalproject.bootcoinnttpf.repository.FXRepository;
import com.finalproject.bootcoinnttpf.service.FXService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class FXServiceImpl implements FXService {

    @Autowired
    private FXRepository fxRepository;

    private final RMapReactive<String, FX> fxMap;

    public FXServiceImpl(RedissonReactiveClient client) {
        this.fxMap = client.getMap("fx", new TypedJsonJacksonCodec(String.class, FX.class));
    }

    @Override
    public Mono<Double> getFx(String currency) {
        return fxMap.get(currency)
                .switchIfEmpty(
                        fxRepository.findFXByCurrency(currency)
                                .switchIfEmpty(Mono.error(new AccountNotFoundException("Not found currency:" + currency)))
                                .next()
                                .flatMap(a -> fxMap.fastPut(currency, a).thenReturn(a))
                ).map(FX::getFx);
    }

    @Override
    public Mono<Double> setFx(FX fx) {
        return fxRepository.findFXByCurrency(fx.getCurrency())
                .switchIfEmpty(Flux.just(new FX(fx.getCurrency(),fx.getFx())))
                .next()
                .flatMap(currencyFound -> {
                    currencyFound.setFx(fx.getFx());
                    return fxMap.fastPut(currencyFound.getCurrency(), currencyFound)
                            .thenReturn(currencyFound);
                })
                .flatMap(fxAux -> fxRepository.save(fxAux))
                .doOnSuccess(x -> System.out.println("Currency saved" + x))
                .map(FX::getFx)
                .doOnError(ex -> log.error("Error updating currency ", ex));
    }

}
