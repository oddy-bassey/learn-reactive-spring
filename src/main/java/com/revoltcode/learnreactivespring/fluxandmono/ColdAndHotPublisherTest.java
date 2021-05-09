package com.revoltcode.learnreactivespring.fluxandmono;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1))
                .log();

        stringFlux.subscribe(element -> log.info("Subscriber 1 : "+element));

        Thread.sleep(3000);

        stringFlux.subscribe(element -> log.info("Subscriber 2 : "+element));

        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1))
                .log();

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(element -> log.info("Subscriber 1 : "+element));

        Thread.sleep(3000);

        connectableFlux.subscribe(element -> log.info("Subscriber 2 : "+element));

        Thread.sleep(4000);
    }
}
