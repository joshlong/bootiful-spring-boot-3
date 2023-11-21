package bootiful.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.client.AiClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;

@EnableAsync
@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

@Component
class AsyncRunner implements ApplicationRunner {

    private final CustomerRepository repository;
    private final Logger log = LoggerFactory.getLogger(getClass());

    AsyncRunner(CustomerRepository repository) {
        this.repository = repository;
    }

    @Async
    @Override
    public void run(ApplicationArguments args) {
        repository.findAll().forEach(c -> log.info(c.toString()));
    }
}

record Customer(@Id Integer id, String name) {
}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
    Collection<Customer> findByName(String name);
}

@Controller
@ResponseBody
class JokeHttpController {

    private final AiClient client;

    JokeHttpController(AiClient client) {
        this.client = client;
    }

    @GetMapping("/joke")
    Map<String, String> joke() {
        return Map.of("message", this.client.generate("tell me a joke about Oslo, Norway"));
    }

    @GetMapping("/seuss")
    Map<String, String> seuss() {
        return Map.of("seussStory", this.client.generate("write a quick story about the power of Spring Boot in beautiful Oslo, Norway, in the style of the author Dr. Seuss"));
    }
}

@Controller
@ResponseBody
class CustomerHttpController {

    private final CustomerRepository repository;

    private final ObservationRegistry registry;

    CustomerHttpController(CustomerRepository repository, ObservationRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    @GetMapping("/customers/{name}")
    Collection<Customer> customersByName(@PathVariable String name) {
        Assert.state(Character.isUpperCase(name.charAt(0)), "the name must start with an uppercase letter");
        return Observation
                .createNotStarted("by-name", this.registry)
                .observe(() -> this.repository.findByName(name));
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.repository.findAll();
    }
}

@ControllerAdvice
class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    ProblemDetail problemDetail(IllegalStateException illegalStateException, HttpServletRequest request) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        pd.setDetail(illegalStateException.getLocalizedMessage());
        request.getHeaderNames().asIterator().forEachRemaining(System.out::println);
        return pd;
    }
}