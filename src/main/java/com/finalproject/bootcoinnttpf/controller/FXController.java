package com.finalproject.bootcoinnttpf.controller;

import com.finalproject.bootcoinnttpf.model.FX;
import com.finalproject.bootcoinnttpf.service.FXService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bootcoin/fx")
@AllArgsConstructor
@Slf4j
public class FXController {
    private FXService service;

    @GetMapping("{currency}")
    public Mono<Double> getFx(@PathVariable final String currency) {
        return service.getFx(currency);
    }

    @GetMapping("{currency}/{amount}")
    public Mono<Double> getFxAmount(@PathVariable final String currency, @PathVariable final Double amount) {
        return service.getFx(currency)
                .map(fx -> fx * amount);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Double> saveAccount(@RequestBody final FX fx) {
        return service.setFx(fx);
    }

    @PutMapping
    public Mono<Double> updateAccount(@RequestBody final FX fx) {
        return service.setFx(fx);
    }

}
