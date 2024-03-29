package jim.net.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "jim.net")
public class HazelcastTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(HazelcastTestApplication.class, args);
	}

}