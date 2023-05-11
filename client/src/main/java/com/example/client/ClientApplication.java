package com.example.client;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
    ApplicationRunner applicationRunner(CustomerClient cc) {
        return args -> cc.all().subscribe(System.out::println);
    }

    @Bean
    CustomerClient customerClient(WebClient.Builder builder) {
        var wc = builder.baseUrl("http://localhost:8080/").build();
        var wca = WebClientAdapter.forClient(wc);
        var hsp = HttpServiceProxyFactory.builder()
                .clientAdapter(wca)
                .build()
                .createClient(CustomerClient.class);
        return hsp;
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder b) {
        return b.routes()
                .route(rs -> rs
                        .path("/proxy")
                        .filters(fs -> fs
                                .setPath("/customers")
                                .retry(10)
                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        )
                        .uri("http://localhost:8080/"))
                .build();
    }
}


record Profile(Integer id) {
}

@Controller
class CustomerGraphqlController {

    private final CustomerClient cc;

    CustomerGraphqlController(CustomerClient cc) {
        this.cc = cc;
    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.cc.all();
    }

    @BatchMapping
    Map<Customer, Profile> profile(List<Customer> customerList) {
        var map = new HashMap<Customer, Profile>();
        for (var c : customerList)
            map.put(c, new Profile(c.id()));
        return map;
    }

    /*@SchemaMapping(typeName = "Customer")
    Profile profile(Customer customer) {
        return new Profile(customer.id());
    }*/
}

interface CustomerClient {

    @GetExchange("/customers/{name}")
    Flux<Customer> byName(@PathVariable String name);

    @GetExchange("/customers")
    Flux<Customer> all();
}


// look ma, no Lombok!
record Customer(Integer id, String name) {
}