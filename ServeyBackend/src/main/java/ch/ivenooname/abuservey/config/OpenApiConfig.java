package ch.ivenooname.abuservey.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(title = "AbuServey API", version = "1.0",
		description = "Umfrage zum Kriegsmaterialgesetz (Abstimmung 29.11.2026)"))
@SecurityScheme(name = "adminKey", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER,
		paramName = "X-Admin-Key")
public class OpenApiConfig {
}
