package com.revoltcode.learnreactivespring.fluxandmono;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

@Slf4j
public class VitualTimeTest {

    @Test
    public void withoutVirtualTime(){
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }

    @Test
    public void withVirtualTime(){
        VirtualTimeScheduler.getOrSet();
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }
}
