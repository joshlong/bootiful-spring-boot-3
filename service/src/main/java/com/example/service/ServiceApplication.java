package com.example.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

@Controller
@ResponseBody
class CustomerHttpController {

    private final CustomerService service;
    private final ObservationRegistry registry;

    CustomerHttpController(CustomerService service, ObservationRegistry registry) {
        this.service = service;
        this.registry = registry;
    }

    @GetMapping("/customers")
    Collection<Customer> all() {
        return Observation
                .createNotStarted("all", this.registry)
                .observe(this.service::all);
    }

    @GetMapping("/customers/{name}")
    Collection<Customer> byName(@PathVariable String name) {
        if (name == null && Character.isUpperCase(name.charAt(0)))
            throw new IllegalArgumentException("the name must be valid");

        return Observation
                .createNotStarted("byName", this.registry)
                .observe(() -> this.service.byName(name));
    }
}

@ControllerAdvice
class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    ProblemDetail handle(IllegalArgumentException iae ) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(503) ,"the name is invalid");
    }
}

@Service
class CustomerService {

    private final JdbcTemplate template;

    private final RowMapper<Customer> customerRowMapper = new RowMapper<Customer>() {

        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(rs.getInt("id"), rs.getString("name"));
        }
    };

    CustomerService(JdbcTemplate template) {
        this.template = template;
    }

    Collection<Customer> byName(String name) {
        return this.template
                .query("select * from customer where name  = ? ", this.customerRowMapper, name);
    }

    Collection<Customer> all() {
        return this.template.query("select * from customer", this.customerRowMapper);
    }
}

record Customer(Integer id, String name) {
}