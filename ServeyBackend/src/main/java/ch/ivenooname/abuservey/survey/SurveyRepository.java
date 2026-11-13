package ch.ivenooname.abuservey.survey;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyRepository {

	private final JdbcClient jdbc;

	public SurveyRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	public void insert(SurveySubmission s) {
		// Werte stammen aus validierten Enums (keine Kommas) -> string_to_array ist sicher
		String sources = s.sources().stream().map(Enum::name).sorted().collect(Collectors.joining(","));
		jdbc.sql("""
				INSERT INTO response (age_group, eligible, informed, sources, export_opinion, top_argument, vote_intent)
				VALUES (:age, :eligible, :informed, string_to_array(:sources, ','), :export, :arg, :vote)
				""")
				.param("age", s.ageGroup() == null ? null : s.ageGroup().name(), Types.VARCHAR)
				.param("eligible", s.eligible(), Types.BOOLEAN)
				.param("informed", s.informed())
				.param("sources", sources)
				.param("export", s.exportOpinion().name())
				.param("arg", s.topArgument().name())
				.param("vote", s.voteIntent().name())
				.update();
	}

	/** Atomar: true, wenn der Token existierte und gerade erstmals eingeloest wurde. */
	public boolean claimToken(String token) {
		if (token == null || token.isBlank()) {
			return false;
		}
		return jdbc.sql("UPDATE invite_token SET used = true WHERE token = :t AND used = false")
				.param("t", token)
				.update() == 1;
	}

	// ---- Auswertung (Admin) ----

	public long count() {
		return jdbc.sql("SELECT count(*) FROM response").query(Long.class).single();
	}

	public Double averageInformed() {
		return jdbc.sql("SELECT avg(informed)::float8 FROM response").query(Double.class).optional().orElse(null);
	}

	/** Folie 8: Abstimmungsabsicht je Altersgruppe. */
	public List<Map<String, Object>> voteByAge() {
		return jdbc.sql("""
				SELECT coalesce(age_group, 'NO_ANSWER') AS age_group,
				       count(*)                                          AS total,
				       count(*) FILTER (WHERE vote_intent = 'YES')        AS yes,
				       count(*) FILTER (WHERE vote_intent = 'RATHER_YES') AS rather_yes,
				       count(*) FILTER (WHERE vote_intent = 'RATHER_NO')  AS rather_no,
				       count(*) FILTER (WHERE vote_intent = 'NO')         AS no,
				       count(*) FILTER (WHERE vote_intent = 'UNDECIDED')  AS undecided
				FROM response GROUP BY 1 ORDER BY 1
				""").query().listOfRows();
	}

	/** Folie 9: Haupt-Argument mit Prozent. */
	public List<Map<String, Object>> arguments() {
		return jdbc.sql("""
				SELECT top_argument, count(*) AS n,
				       round(100.0 * count(*) / sum(count(*)) OVER (), 1) AS percent
				FROM response GROUP BY top_argument ORDER BY n DESC
				""").query().listOfRows();
	}

	public List<Map<String, Object>> sourceCounts() {
		return jdbc.sql("SELECT s AS source, count(*) AS n FROM response, unnest(sources) s GROUP BY s ORDER BY n DESC")
				.query().listOfRows();
	}

	public List<Map<String, Object>> exportOpinions() {
		return jdbc.sql("SELECT export_opinion, count(*) AS n FROM response GROUP BY 1 ORDER BY n DESC")
				.query().listOfRows();
	}
}
