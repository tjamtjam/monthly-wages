package tjamtjam.monthlywages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@SpringBootApplication
public class MonthlyWagesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonthlyWagesApplication.class, args);
	}
	
	/*
	 * Instantiate Java 8 time dialect for Thymeleaf so that we can format LocalDate with Thymeleaf.
	 */
	@Bean
	public Java8TimeDialect java8TimeDialect() {
		return new Java8TimeDialect();
	}
}
