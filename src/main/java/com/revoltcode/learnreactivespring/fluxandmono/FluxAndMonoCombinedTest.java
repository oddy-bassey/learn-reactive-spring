package com.revoltcode.learnreactivespring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombinedTest {

    /*
     * In reality, you might be making two different db or external services calls
     * and you may want to combine this Fluxs to one and then return that to the caller
     */

    @Test
    public void combinedUsingMerge(){

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergeFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combinedUsingMergeWithDelay(){

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combinedUsingConcat(){

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergeFlux = Flux.concat(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combinedUsingConcatWithDelay(){

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux.concat(flux1, flux2).log();

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combinedUsingConcatWithDelayAndEnabledVirtualTime(){

        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergeFlux = Flux.concat(flux1, flux2).log();

        StepVerifier.withVirtualTime(() -> mergeFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combinedUsingZip(){

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergeFlux = Flux.zip(flux1, flux2, (t1, t2) -> {
            return t1.concat(t2); // AD, BE, CF
        }).log(); // A,D : B,E : C,F

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
