package ch.ivenooname.abuservey.admin;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.ivenooname.abuservey.survey.SurveyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Auswertung", description = "Nur mit Header X-Admin-Key")
@SecurityRequirement(name = "adminKey")
public class AdminController {

	private final SurveyRepository repository;

	public AdminController(SurveyRepository repository) {
		this.repository = repository;
	}

	public record Results(
			long total,
			Double averageInformed,
			List<Map<String, Object>> voteByAge,
			List<Map<String, Object>> arguments,
			List<Map<String, Object>> sources,
			List<Map<String, Object>> exportOpinions) {
	}

	@Operation(summary = "Gesamtauswertung (Folien 8 und 9 u.a.)")
	@GetMapping("/results")
	public Results results() {
		return new Results(
				repository.count(),
				repository.averageInformed(),
				repository.voteByAge(),
				repository.arguments(),
				repository.sourceCounts(),
				repository.exportOpinions());
	}
}
