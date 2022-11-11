package com.example.client;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    CustomerClient client(WebClient.Builder builder) {
        return HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(builder.baseUrl("http://localhost:8080/").build()))
                .build()
                .createClient(CustomerClient.class);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder rlb) {
        return rlb
                .routes()
                .route(rs -> rs.path("/proxy")
                        .filters(f -> f.setPath("/customers"))
                        .uri("http://localhost:8080/"))
                .build();
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> readyListener(CustomerClient client) {
        return event -> client.all().subscribe(System.out::println);
    }

}


class CustomerHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        List.of(Customer.class, CustomerGraphqlController.class).forEach(c -> hints.reflection().registerType(c, MemberCategory.values()));
        Set.of(new ClassPathResource("graphiql/index.html"), new ClassPathResource("graphql/schema.graphqls"))
                .forEach(s -> hints.resources().registerResource(s));
    }
}

@Controller
@ResponseBody
@ImportRuntimeHints(CustomerHints.class)
class CustomerGraphqlController {

    final CustomerClient client;

    CustomerGraphqlController(CustomerClient client) {
        this.client = client;
    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.client.all();
    }

    @QueryMapping
    Flux<Customer> customersByName(@Argument String name) {
        return this.client.byName(name);
    }
}

interface CustomerClient {

    @GetExchange("/customers")
    Flux<Customer> all();

    @GetExchange("/customers/{name}")
    Flux<Customer> byName(@PathVariable String name);
}

record Customer(Integer id, String name) {
}
