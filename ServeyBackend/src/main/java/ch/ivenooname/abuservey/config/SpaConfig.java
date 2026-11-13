package ch.ivenooname.abuservey.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Liefert das eingebettete Angular-Frontend aus und faellt bei Client-Routen (z.B. /danke)
 * auf index.html zurueck. /api, Swagger und Actuator-aehnliche Pfade sind ausgenommen.
 */
@Configuration
public class SpaConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/META-INF/resources/")
				.resourceChain(true)
				.addResolver(new PathResourceResolver() {
					@Override
					protected Resource getResource(String resourcePath, Resource location) throws IOException {
						Resource requested = location.createRelative(resourcePath);
						if (requested.exists() && requested.isReadable()) {
							return requested;
						}
						if (isClientRoute(resourcePath)) {
							Resource index = new ClassPathResource("META-INF/resources/index.html");
							return index.exists() ? index : null;
						}
						return null;
					}
				});
	}

	private static boolean isClientRoute(String path) {
		return !path.startsWith("api/")
				&& !path.startsWith("v3/")
				&& !path.startsWith("swagger-ui")
				&& !path.contains(".");
	}
}
