package com.example.service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Configuration
class Threads {

	private static void run(boolean first, Set<String> names) {
		if (first)
			names.add(Thread.currentThread().toString());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	// stolen from Oracle Java Developer Advocate and professor José Paumard

	@Bean
	ApplicationRunner demo() {
		return args -> {

			// store all 1,000 threads
			var threads = new ArrayList<Thread>();

			// dedupe elements with Set<T>
			var names = new ConcurrentSkipListSet<String>();

			// merci José Paumard d'Oracle
			for (var i = 0; i < 1000; i++) {
				var first = 0 == i;
				threads.add(Thread.ofPlatform().unstarted(() ->  {
					run(first, names); 
					run(first, names); 
					run(first, names); 
					run(first, names); 
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
