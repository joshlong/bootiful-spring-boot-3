package com.example.client;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder b) {
        return b
                .routes()
                .route(rs -> rs
                                .path("/proxy")
                                .filters(f -> f
                                                .setPath("/customers")
                                                .retry(10)
                                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
//                                        .requestRateLimiter(null)
//                                        .circuitBreaker( null)
//                                        .jsonToGRPC( null)
//                                        .tokenRelay()
//
                                )
                                .uri("http://localhost:8080/")
                )
                .build();
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerHttpClient http) {
        return a -> http.customers().subscribe(System.out::println);
    }

    @Bean
    CustomerHttpClient customerHttpClient(WebClient.Builder builder) {
        var wc = builder.baseUrl("http://localhost:8080/").build();
        return HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(wc))
                .build()
                .createClient(CustomerHttpClient.class);
    }

}


@Controller
class CustomerGraphqlController {

    private final CustomerHttpClient http;

    CustomerGraphqlController(CustomerHttpClient http) {
        this.http = http;
    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.http.customers();
    }

    @QueryMapping
    Flux<Customer> customersByName(@Argument String name) {
        return this.http.customersByName(name);
    }

    @BatchMapping(typeName = "Customer")
    Map<Customer, Profile> profile(List<Customer> customer) throws Exception {
        // calls http profile service
        var map = new HashMap<Customer, Profile>() ;
        for (var c : customer)
            map.put( c, new Profile(c.id()));
        System.out.println("getting ALL profiles for [" + customer +
                           "]");
        return map;
    }
}

record Profile(Integer id) {
}

interface CustomerHttpClient {

    @GetExchange("/customers/{name}")
    Flux<Customer> customersByName(@PathVariable String name);

    @GetExchange("/customers")
    Flux<Customer> customers();

}


// look ma, no Lombok!!
record Customer(Integer id, String name) {
}