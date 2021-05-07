package com.revoltcode.learnreactivespring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void fluxTest(){
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring");
        stringFlux.subscribe(System.out::println);
    }
}
