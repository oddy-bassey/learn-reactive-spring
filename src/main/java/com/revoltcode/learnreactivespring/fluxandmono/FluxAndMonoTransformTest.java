package com.revoltcode.learnreactivespring.fluxandmono;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

@Slf4j
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

    @Test
    public void transformUsingMap_LengthRepeat() {

        Flux<Integer> nameFlux = Flux.fromIterable(names)
                .map(name -> name.length())
                .repeat(1)
                .log();

        StepVerifier.create(nameFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();

    }

    @Test
    public void transformUsingFlapMap() {

        Flux<String> nameFlux = Flux.fromIterable(Arrays.asList("A", "B", "c", "D", "E", "F"))
                .flatMap(letter -> {
                    return Flux.fromIterable(convertToList(letter)); // A -> List[A, newValue], B -> List/\[B, newValue]
                })
                .log(); //for all db or external service call that returns a flux<String>

        StepVerifier.create(nameFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlapMapParallelProcessing() {
        /*
         * Here the goal is to achieve a more faster execution time by
         * running parallel processes
         *
         * NOTE: The outcome of this is a reduced execution time but
         *       the order of the data is lost.
         */
        Flux<String> nameFlux = Flux.fromIterable(Arrays.asList("A", "B", "c", "D", "E", "F"))
                .window(2) //Flux<Flux<String>> (A,B), (C,D), (E,F)
                .flatMap((letter) ->
                    letter.map(this::convertToList).subscribeOn(parallel())) //Flux<List<String>>
                .flatMap(data -> Flux.fromIterable(data)) //Flux<String>
                .log(); //for all db or external service call that returns a flux<String>

        StepVerifier.create(nameFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlapMapParallelProcessingMaintainOrder() {

        /*
         * Here the goal is to achieve a more faster execution time by running
         * parallel processes and as well maintain the order of the data by
         * using concatMap(...)
         *
         * NOTE: The outcome of this is that the execution time increases
         */
        Flux<String> nameFlux = Flux.fromIterable(Arrays.asList("A", "B", "c", "D", "E", "F"))
                .window(2) //Flux<Flux<String>> (A,B), (C,D), (E,F)
                //.concatMap((letter) -> //Maintained order but increased execution time
                .flatMapSequential((letter) ->
                        letter.map(this::convertToList).subscribeOn(parallel())) //Flux<List<String>>
                .flatMap(data -> Flux.fromIterable(data)) //Flux<String>
                .log(); //for all db or external service call that returns a flux<String>

        StepVerifier.create(nameFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String data) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Arrays.asList(data, "newValue");
    }
}