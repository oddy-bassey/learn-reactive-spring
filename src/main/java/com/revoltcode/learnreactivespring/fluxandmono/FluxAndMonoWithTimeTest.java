package com.revoltcode.learnreactivespring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .log(); //starts from 0 --> .....

        infiniteFlux.subscribe(element -> System.out.println("Value is : "+element));
        Thread.sleep(2000);
    }

    @Test
    public void infiniteSequenceTest(){
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3) //defines the number o element the flux will take
                .log(); //starts from 0 --> .....

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMapTest(){
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .map(element -> new Integer(element.intValue()))
                .take(3)
                .log(); //starts from 0 --> .....

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMapWithDelay(){
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(element -> new Integer(element.intValue()))
                .take(3)
                .log(); //starts from 0 --> .....

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
