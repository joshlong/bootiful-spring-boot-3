package bootiful.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import org.springframework.ai.client.AiClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }


    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {

            // tiré de M. José Paumard (le professeur)
            var es = Executors.newVirtualThreadPerTaskExecutor();


            var observedThreadNames = new ConcurrentSkipListSet<String>();
            var threads = new ArrayList<Thread>();
            for (var i = 0; i < 100_000; i++) { // green/virtual threads are cheap!
                var index = i;
                threads.add(Thread.ofVirtual().unstarted(() -> {
                    for (var x = 0; x < 5; x++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if (index == 0) observedThreadNames.add(Thread.currentThread().toString());
                    }
                }));

            }

            for (var t : threads) t.start();

            for (var t : threads) t.join();

            System.out.println(observedThreadNames.toString());


        };
    }


    String displayMessageForUsersLoan(Loan loan) {
        return switch (loan) {
            case SecuredLoan sl -> "you got a very good interest rate! Well done! ";
            case UnsecuredLoan(var interest) -> "the interest rate that you got, " + interest + "%, is going to hurt!";
        };
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.requestFactory(new JdkClientHttpRequestFactory()).build();
    }

    @Bean
    CatFactClient catFactClient(RestClient restClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build().createClient(CatFactClient.class);
    }
}

@Controller
@ResponseBody
class StoryController {

    private final AiClient singularity;

    private final ObservationRegistry registry;

    StoryController(AiClient singularity, ObservationRegistry registry) {
        this.singularity = singularity;
        this.registry = registry;
    }

    @GetMapping("/story")
    Map<String, String> story() {
        var start = Thread.currentThread().toString();
        System.out.println(start);
        var prompt = """
                                
                Dear Singularity,
                                
                Would you please tell me a story about the lovely people in the amazing Brazillian cities of
                Rio de Janeiro, Sao Paulo, and Brazilla. And please make mention of the amazing food, culture, 
                weather, coffee, and more. Also, please tell the story in the style of famed Children's author
                Dr. Seuss.
                                
                Obrigado,
                Josh
                                
                """;

        var result = Observation
                .start("story-time", this.registry) // story-time
                .observe(() -> Map.of("story", this.singularity.generate(prompt))); // distributed tracing

        System.out.println("after [" + start + "]:" + Thread.currentThread());

        return result;
    }
}

/**
 * DATA ORIENTED PROGRAMMING
 * 1) record
 * 2) sealed types
 * 3) pattern matching
 * 4) smart switch expressions
 */

@Controller
@ResponseBody
class CatFactController {

    private final CatFactClient cfc;

    CatFactController(CatFactClient cfc) {
        this.cfc = cfc;
    }

    @GetMapping("/cats")
    CatFact fact() {
        return this.cfc.fact();
    }
}

interface CatFactClient {

    @Observed(name = "cat-facts-ftw")
    @GetExchange("https://catfact.ninja/fact")
    CatFact fact();

}

record CatFact(String fact) {
}


sealed interface Loan permits UnsecuredLoan, SecuredLoan {
}

final class SecuredLoan implements Loan {
}

// java is a NOMINAL language: everything has names
record UnsecuredLoan(float interest) implements Loan {
}

@Controller
@ResponseBody
class CustomerController {

    private final CustomerRepository repository;

    CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.repository.customers();
    }
}

@Repository
class CustomerRepository {

    private final JdbcClient jdbc;

    CustomerRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    Collection<Customer> customers() {
        return this.jdbc.sql("""
                select * from customer        
                """).query((rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name"))).set();
    }

}

// look mom, no Lombok!
record Customer(Integer id, String name) {
}
