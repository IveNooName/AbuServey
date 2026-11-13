package ch.ivenooname.abuservey.survey;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;

import ch.ivenooname.abuservey.admin.AdminController;
import ch.ivenooname.abuservey.config.SecurityConfig;

@WebMvcTest({ SurveyController.class, AdminController.class })
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.admin-key=secret")
class SurveyControllerTest {

	private static final String VALID = """
			{"ageGroup":"AGE_18_25","eligible":true,"informed":4,"sources":["MEDIA","SOCIAL_MEDIA"],
			 "exportOpinion":"STRICT_CONDITIONS","topArgument":"SECURITY","voteIntent":"RATHER_NO"}
			""";

	@Autowired
	MockMvc mvc;

	@MockitoBean
	SurveyService service;

	@MockitoBean
	SurveyRepository repository;

	@Test
	void statusWithoutCookieIsFalse() throws Exception {
		mvc.perform(get("/api/survey/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.alreadyParticipated").value(false));
	}

	@Test
	void successfulSubmitSetsThirtyDayHttpOnlyCookie() throws Exception {
		mvc.perform(post("/api/survey/responses").contentType(MediaType.APPLICATION_JSON).content(VALID))
				.andExpect(status().isCreated())
				.andExpect(cookie().httpOnly("survey_done", true))
				.andExpect(cookie().maxAge("survey_done", 30 * 24 * 3600));
		verify(service).submit(any());
	}

	@Test
	void submitWithCookieIsConflict() throws Exception {
		mvc.perform(post("/api/survey/responses").cookie(new Cookie("survey_done", "1"))
				.contentType(MediaType.APPLICATION_JSON).content(VALID))
				.andExpect(status().isConflict());
		verify(service, never()).submit(any());
	}

	@Test
	void noneCombinedWithOtherSourceIsRejected() throws Exception {
		String bad = VALID.replace("[\"MEDIA\",\"SOCIAL_MEDIA\"]", "[\"NONE\",\"MEDIA\"]");
		mvc.perform(post("/api/survey/responses").contentType(MediaType.APPLICATION_JSON).content(bad))
				.andExpect(status().isBadRequest());
	}

	@Test
	void informedOutOfRangeIsRejected() throws Exception {
		mvc.perform(post("/api/survey/responses").contentType(MediaType.APPLICATION_JSON)
				.content(VALID.replace("\"informed\":4", "\"informed\":7")))
				.andExpect(status().isBadRequest());
	}

	@Test
	void adminWithoutKeyIsUnauthorized() throws Exception {
		mvc.perform(get("/api/admin/results")).andExpect(status().isUnauthorized());
	}

	@Test
	void adminWithWrongKeyIsUnauthorized() throws Exception {
		mvc.perform(get("/api/admin/results").header("X-Admin-Key", "nope")).andExpect(status().isUnauthorized());
	}

	@Test
	void adminWithKeyIsOk() throws Exception {
		mvc.perform(get("/api/admin/results").header("X-Admin-Key", "secret")).andExpect(status().isOk());
	}
}
