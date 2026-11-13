package ch.ivenooname.abuservey.survey;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Eine ausgefuellte Umfrage. Alle Felder ausser Alter/Stimmberechtigung sind Pflicht. */
public record SurveySubmission(
		@Schema(description = "Altersgruppe (freiwillig)") AgeGroup ageGroup,
		@Schema(description = "Stimmberechtigt? (freiwillig)") Boolean eligible,
		@Schema(description = "Wie gut informiert, 1 (gar nicht) bis 5 (sehr gut)", minimum = "1", maximum = "5")
		@NotNull @Min(1) @Max(5) Integer informed,
		@Schema(description = "Informationsquellen; NONE nur allein")
		@NotEmpty Set<Source> sources,
		@NotNull ExportOpinion exportOpinion,
		@NotNull TopArgument topArgument,
		@NotNull VoteIntent voteIntent,
		@Schema(description = "Einladungs-Token (nur noetig wenn app.tokens.required=true)")
		@Size(max = 64) String token) {

	@JsonIgnore
	@Schema(hidden = true)
	@AssertTrue(message = "NONE darf nicht mit anderen Quellen kombiniert werden")
	public boolean isSourcesConsistent() {
		return sources == null || !sources.contains(Source.NONE) || sources.size() == 1;
	}
}
