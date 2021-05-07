package com.revoltcode.learnreactivespring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Jenny");

    @Test
    public void filterTest(){
        Flux<String> nameFlux = Flux.fromIterable(names)
                .filter(name -> name.startsWith("A"))
                //.filter(name -> name.length() > 4)
                .log();

        StepVerifier.create(nameFlux)
                .expectNext("Adam", "Anna")
                .verifyComplete();
    }
}
