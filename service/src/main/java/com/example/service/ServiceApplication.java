package com.example.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.client.AiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}

@ResponseBody
@Controller
class JokesController {

    private final AiClient aiClient;

    JokesController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @GetMapping("/jokes")
    String jokes() {
        return this.aiClient.generate("tell me a joke about seasons");
    }
}

@Controller
@ResponseBody
class CustomerController {

    private final CustomerRepository repository;

    private final ObservationRegistry registry;


    CustomerController(CustomerRepository repository, ObservationRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    @GetMapping("/customers/{name}")
    Collection<Customer> customersByName(@PathVariable String name) {
        return Observation
                .createNotStarted("by-name", this.registry) // metric
                .observe(() -> repository.findByName(name)); // tracing
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.repository.findAll();
    }


}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
    Collection<Customer> findByName(String name);

}

// look mom, no Lombok!
record Customer(@Id Integer id, String name) {
}