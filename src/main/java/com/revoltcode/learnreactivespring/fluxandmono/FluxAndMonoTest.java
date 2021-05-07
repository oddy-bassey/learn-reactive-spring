package com.revoltcode.learnreactivespring.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void fluxTest(){
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.just("After error"))
                .log();

        stringFlux.subscribe(System.out::println, (e) -> System.out.println("Exception is "+e),
                () -> System.out.println("Completed!"));
    }
}
