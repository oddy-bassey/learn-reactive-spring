package com.revoltcode.learnreactivespring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

    @Test
    public void transformUsingMap() {

        Flux<Integer> nameFlux = Flux.fromIterable(names)
                //.map(name -> name.toUpperCase())
                .map(name -> name.length())
                .log();

        StepVerifier.create(nameFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapFilter() {

        Flux<String> nameFlux = Flux.fromIterable(names)
                .filter(name -> name.length() > 4)
                .map(name -> name.toUpperCase())
                .log();

        StepVerifier.create(nameFlux)
                .expectNext("JENNY")
                .verifyComplete();

    }
}