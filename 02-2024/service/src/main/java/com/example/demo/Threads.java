package com.example.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

@Configuration
class Threads {

	@Bean
	ApplicationRunner demo() {
		return args -> {

			var threads = new ArrayList<Thread>();

			var names = new ConcurrentSkipListSet<String>();

			// merci José Paumard d'Oracle

			for (var i = 0; i < 1000; i++) {
				var first = 0 == i;
				threads.add(Thread.ofVirtual().unstarted(() -> {
					if (first)
						names.add(Thread.currentThread().toString());
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						throw new RuntimeException(e);
					}

					if (first)
						names.add(Thread.currentThread().toString());
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						throw new RuntimeException(e);
					}

					if (first)
						names.add(Thread.currentThread().toString());
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						throw new RuntimeException(e);
					}

					if (first)
						names.add(Thread.currentThread().toString());
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}));
			}

			for (var t : threads)
				t.start();

			for (var t : threads)
				t.join();

			System.out.println(names);
		};
	}

}
