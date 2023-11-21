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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder rlb) {
        return rlb
                .routes()
                .route(rs ->
                        rs
                                .path("/proxy")
                                .filters(fs -> fs
                                        .setPath("/customers")
                                        .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                        .retry(10)
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
    CustomerHttpClient client(WebClient.Builder builder) {
        return HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(
                        builder.baseUrl("http://localhost:8080").build()
                ))
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
    Flux<Customer> customersByName(@Argument String name) {
        return this.http.customersByName(name);
    }


    @BatchMapping(typeName = "Customer")
    Map<Customer, Profile> profile(List<Customer> customer) {

        var map = new HashMap<Customer ,Profile>() ;
        for (var c : customer)
            map.put( c, new Profile(c.id())) ;
        System.out.println("getting ALL customers " +customer);
        return map ;
    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.http.customers();
    }
}


record Profile(Integer id) {
}

// look mom, no Lombok!
record Customer(Integer id, String name) {
}

interface CustomerHttpClient {

    @GetExchange("/customers")
    Flux<Customer> customers();

    @GetExchange("/customers/{name}")
    Flux<Customer> customersByName(@PathVariable String name);
}
