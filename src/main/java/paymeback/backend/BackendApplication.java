package paymeback.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

  @Override
  public void run(String... args) {
    BigDecimal sum = new BigDecimal(100);
    BigDecimal split = sum.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    System.out.println(split);
  }
}
