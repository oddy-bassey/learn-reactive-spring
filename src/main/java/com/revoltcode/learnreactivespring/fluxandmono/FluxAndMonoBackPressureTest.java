package com.revoltcode.learnreactivespring.fluxandmono;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest(){

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure(){
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(element -> log.info("Element is "+element), //actual element
                ex -> log.info("Exception is : "+ex), //error handling
                () -> log.info("Done!"), //completion  event
                subscription -> subscription.request(2)); //actual subscription
    }

    @Test
    public void backPressureCancel(){
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(element -> log.info("Element is "+element), //actual element
                ex -> log.info("Exception is : "+ex), //error handling
                () -> log.info("Done!"), //completion  event
                subscription -> subscription.cancel()); //actual subscription
    }

    @Test
    public void customizedBackPressureCancel(){
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                log.info("Value received is : "+value);
                if(value == 4){
                    cancel();
                }
            }
        });
    }
}
