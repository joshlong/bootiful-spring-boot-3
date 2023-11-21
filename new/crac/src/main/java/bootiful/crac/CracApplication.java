package bootiful.crac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
public class CracApplication {

    public static void main(String[] args) {
        SpringApplication.run(CracApplication.class, args);
    }

}


//org.crac.Resource
@Component
class StatefulComponent implements SmartLifecycle {

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void stop() {
        if (this.running.compareAndSet(true, false))
            System.out.println("closing sensitive resources down before snapshot");
    }

    @Override
    public void start() {
        if (this.running.compareAndSet(false, true))
            System.out.println("restoring..");
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }
}