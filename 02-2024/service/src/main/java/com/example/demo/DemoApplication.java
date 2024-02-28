package com.example.demo;

import org.springframework.ai.chat.ChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@Controller
@ResponseBody
class StoryController {

    private final ChatClient singularity;

    StoryController(ChatClient singularity) {
        this.singularity = singularity;
    }

    @GetMapping("/story")
    Map<String, String> story() {
        var prompt = """
                Dear Singularity,
                                
                Please write a story about the good folks of San Francisco, capital of all things Artificial Intelligence, 
                and please do so in the style of famed children's author Dr. Seuss.
                                
                Cordially,
                Josh Long
                """;
        var reply = this.singularity.call(prompt);
        return Map.of("message", reply);
    }
}


@Controller
@ResponseBody
class CustomerHttpController {

    private final CustomerRepository repository;

    CustomerHttpController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.repository.findAll();
    }
}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String name) {
}


class Loans {

    sealed interface Loan permits SecuredLoan, UnsecuredLoan {
    }

    record UnsecuredLoan(float interest) implements Loan {
    }

    final class SecuredLoan implements Loan {
    }

    String displayMessageFor(Loan loan) {
        return switch (loan) {
            case SecuredLoan sl -> "good job! ";
            case UnsecuredLoan(var interest) -> "ouch! that " + interest + "% interest rate is going to hurt!";
        };
    }
}

@Configuration
class Threads {

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {

            var threads = new ArrayList<Thread>();
            var names = new ConcurrentSkipListSet<String>();
            // merci José Paumard, Oracle
            for (var i = 0; i < 1000; i++) {
                var first = 0 == i;
                threads.add(Thread.ofVirtual().unstarted(() -> {
                    if (first) names.add(Thread.currentThread().toString());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (first) names.add(Thread.currentThread().toString());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (first) names.add(Thread.currentThread().toString());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (first) names.add(Thread.currentThread().toString());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }

            for (var t : threads) t.start();

            for (var t : threads) t.join();

            System.out.println(names);
        };
    }

}