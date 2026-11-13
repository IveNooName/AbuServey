package ch.ivenooname.abuservey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ServeyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServeyBackendApplication.class, args);
	}

}
