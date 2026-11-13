package ch.ivenooname.abuservey.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** Eigene Einstellungen, Prefix "app" (siehe application.yaml). */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
		@DefaultValue Cookie cookie,
		@DefaultValue Tokens tokens,
		/** Schluessel fuer /api/admin/** (Header X-Admin-Key). Leer = Admin-Endpunkte gesperrt. */
		@DefaultValue("") String adminKey) {

	public record Cookie(
			@DefaultValue("survey_done") String name,
			@DefaultValue("30d") Duration maxAge,
			/** Auf true setzen, sobald HTTPS ausgeliefert wird. */
			@DefaultValue("false") boolean secure) {
	}

	public record Tokens(
			/** true = nur mit gueltigem, noch nicht benutztem Einladungs-Token teilnehmen. */
			@DefaultValue("false") boolean required) {
	}
}
