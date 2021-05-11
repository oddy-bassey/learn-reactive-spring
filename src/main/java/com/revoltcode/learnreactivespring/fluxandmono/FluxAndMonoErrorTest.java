package com.revoltcode.learnreactivespring.fluxandmono;

import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorResume(ex -> {
                    System.out.println("Exeption is : "+ex);
                    return Flux.just("Default", "Default1");
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Default", "Default1")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingOnErrorReturn(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("Default")
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingOnErrorMap(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap( ex -> new CustomException(ex.getMessage()))
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingOnErrorMapWithRetry(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap( ex -> new CustomException(ex.getMessage()))
                .retry(2)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingOnErrorMapWithRetryBackOff(){
        Retry retrySpec = Retry.fixedDelay(2, Duration.ofMillis(1000))
                .filter((ex) -> ex instanceof CustomException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));

        Flux<String> stringFlux;
        stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap( ex -> new CustomException(ex.getMessage()))
                .retryWhen(retrySpec)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }
}
