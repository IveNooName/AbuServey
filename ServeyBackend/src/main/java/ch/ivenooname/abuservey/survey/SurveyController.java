package ch.ivenooname.abuservey.survey;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.ivenooname.abuservey.config.AppProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/survey")
@Tag(name = "Umfrage", description = "Oeffentliche Endpunkte fuer Teilnehmende")
public class SurveyController {

	private final SurveyService service;
	private final AppProperties props;

	public SurveyController(SurveyService service, AppProperties props) {
		this.service = service;
		this.props = props;
	}

	@Operation(summary = "Hat dieser Browser schon teilgenommen?",
			description = "Der Cookie ist HttpOnly, Angular kann ihn nicht lesen - darum diese Abfrage.")
	@GetMapping("/status")
	public StatusResponse status(HttpServletRequest request) {
		return new StatusResponse(hasParticipated(request));
	}

	@Operation(summary = "Antworten abgeben",
			description = "Setzt bei Erfolg einen 30-Tage-Cookie. Mit vorhandenem Cookie: 409.")
	@ApiResponse(responseCode = "201", description = "Gespeichert, Cookie gesetzt")
	@ApiResponse(responseCode = "400", description = "Validierungsfehler")
	@ApiResponse(responseCode = "403", description = "Token ungueltig/benutzt (falls Token-Pflicht aktiv)")
	@ApiResponse(responseCode = "409", description = "Dieser Browser hat bereits teilgenommen")
	@PostMapping("/responses")
	public ResponseEntity<Void> submit(@Valid @RequestBody SurveySubmission submission, HttpServletRequest request) {
		if (hasParticipated(request)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Du hast bereits teilgenommen");
		}
		service.submit(submission);

		AppProperties.Cookie c = props.cookie();
		ResponseCookie cookie = ResponseCookie.from(c.name(), "1")
				.httpOnly(true)
				.secure(c.secure())
				.sameSite("Lax")
				.path("/")
				.maxAge(c.maxAge())
				.build();
		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.build();
	}

	private boolean hasParticipated(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return false;
		}
		return Arrays.stream(request.getCookies()).anyMatch(k -> props.cookie().name().equals(k.getName()));
	}
}
